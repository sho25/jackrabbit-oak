begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|segment
operator|.
name|standby
operator|.
name|store
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|osgi
operator|.
name|service
operator|.
name|component
operator|.
name|annotations
operator|.
name|ReferencePolicy
operator|.
name|STATIC
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|osgi
operator|.
name|service
operator|.
name|component
operator|.
name|annotations
operator|.
name|ReferencePolicyOption
operator|.
name|GREEDY
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Dictionary
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Hashtable
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|StandardSystemProperty
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|io
operator|.
name|Closer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|segment
operator|.
name|SegmentStore
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|segment
operator|.
name|SegmentStoreProvider
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|segment
operator|.
name|file
operator|.
name|FileStore
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|segment
operator|.
name|standby
operator|.
name|client
operator|.
name|StandbyClientSync
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|segment
operator|.
name|standby
operator|.
name|server
operator|.
name|StandbyServerSync
import|;
end_import

begin_import
import|import
name|org
operator|.
name|osgi
operator|.
name|framework
operator|.
name|ServiceRegistration
import|;
end_import

begin_import
import|import
name|org
operator|.
name|osgi
operator|.
name|service
operator|.
name|component
operator|.
name|ComponentContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|osgi
operator|.
name|service
operator|.
name|component
operator|.
name|annotations
operator|.
name|Activate
import|;
end_import

begin_import
import|import
name|org
operator|.
name|osgi
operator|.
name|service
operator|.
name|component
operator|.
name|annotations
operator|.
name|Component
import|;
end_import

begin_import
import|import
name|org
operator|.
name|osgi
operator|.
name|service
operator|.
name|component
operator|.
name|annotations
operator|.
name|ConfigurationPolicy
import|;
end_import

begin_import
import|import
name|org
operator|.
name|osgi
operator|.
name|service
operator|.
name|component
operator|.
name|annotations
operator|.
name|Deactivate
import|;
end_import

begin_import
import|import
name|org
operator|.
name|osgi
operator|.
name|service
operator|.
name|component
operator|.
name|annotations
operator|.
name|Reference
import|;
end_import

begin_import
import|import
name|org
operator|.
name|osgi
operator|.
name|service
operator|.
name|metatype
operator|.
name|annotations
operator|.
name|AttributeDefinition
import|;
end_import

begin_import
import|import
name|org
operator|.
name|osgi
operator|.
name|service
operator|.
name|metatype
operator|.
name|annotations
operator|.
name|Designate
import|;
end_import

begin_import
import|import
name|org
operator|.
name|osgi
operator|.
name|service
operator|.
name|metatype
operator|.
name|annotations
operator|.
name|ObjectClassDefinition
import|;
end_import

begin_import
import|import
name|org
operator|.
name|osgi
operator|.
name|service
operator|.
name|metatype
operator|.
name|annotations
operator|.
name|Option
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_class
annotation|@
name|Component
argument_list|(
name|configurationPolicy
operator|=
name|ConfigurationPolicy
operator|.
name|REQUIRE
argument_list|)
annotation|@
name|Designate
argument_list|(
name|ocd
operator|=
name|StandbyStoreService
operator|.
name|Configuration
operator|.
name|class
argument_list|)
specifier|public
class|class
name|StandbyStoreService
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|StandbyStoreService
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|BLOB_CHUNK_SIZE
init|=
name|Integer
operator|.
name|getInteger
argument_list|(
literal|"oak.standby.blob.chunkSize"
argument_list|,
literal|1024
operator|*
literal|1024
argument_list|)
decl_stmt|;
annotation|@
name|ObjectClassDefinition
argument_list|(
name|name
operator|=
literal|"Apache Jackrabbit Oak Segment Tar Cold Standby Service"
argument_list|,
name|description
operator|=
literal|"Provides continuous backups of repositories based on Segment Tar"
argument_list|)
annotation_defn|@interface
name|Configuration
block|{
annotation|@
name|AttributeDefinition
argument_list|(
name|name
operator|=
literal|"Persist configuration"
argument_list|,
name|description
operator|=
literal|"Must be always disabled to avoid storing the configuration in the repository"
argument_list|)
name|boolean
name|org_apache_sling_installer_configuration_persist
parameter_list|()
default|default
literal|false
function_decl|;
annotation|@
name|AttributeDefinition
argument_list|(
name|name
operator|=
literal|"Mode"
argument_list|,
name|description
operator|=
literal|"TarMK Cold Standby mode (primary or standby)"
argument_list|,
name|options
operator|=
block|{
annotation|@
name|Option
argument_list|(
name|label
operator|=
literal|"primary"
argument_list|,
name|value
operator|=
literal|"primary"
argument_list|)
block|,
annotation|@
name|Option
argument_list|(
name|label
operator|=
literal|"standby"
argument_list|,
name|value
operator|=
literal|"standby"
argument_list|)
block|}
argument_list|)
name|String
name|mode
parameter_list|()
default|default
literal|"primary"
function_decl|;
annotation|@
name|AttributeDefinition
argument_list|(
name|name
operator|=
literal|"Port"
argument_list|,
name|description
operator|=
literal|"TCP/IP port to use"
argument_list|)
name|int
name|port
parameter_list|()
default|default
literal|8023
function_decl|;
annotation|@
name|AttributeDefinition
argument_list|(
name|name
operator|=
literal|"Primary Host"
argument_list|,
name|description
operator|=
literal|"Primary host (standby mode only)"
argument_list|)
name|String
name|primary_host
parameter_list|()
default|default
literal|"127.0.0.1"
function_decl|;
annotation|@
name|AttributeDefinition
argument_list|(
name|name
operator|=
literal|"Sync interval (seconds)"
argument_list|,
name|description
operator|=
literal|"Sync interval in seconds (standby mode only)"
argument_list|)
name|int
name|interval
parameter_list|()
default|default
literal|5
function_decl|;
annotation|@
name|AttributeDefinition
argument_list|(
name|name
operator|=
literal|"Allowed IP-Ranges"
argument_list|,
name|description
operator|=
literal|"Accept incoming requests for these host names and IP-ranges only (primary mode only)"
argument_list|,
name|cardinality
operator|=
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
name|String
index|[]
name|primary_allowed$_$client$_$ip$_$ranges
argument_list|()
expr|default
block|{}
expr_stmt|;
annotation|@
name|AttributeDefinition
argument_list|(
name|name
operator|=
literal|"Secure"
argument_list|,
name|description
operator|=
literal|"Use secure connections"
argument_list|)
name|boolean
name|secure
parameter_list|()
default|default
literal|false
function_decl|;
annotation|@
name|AttributeDefinition
argument_list|(
name|name
operator|=
literal|"Standby Read Timeout"
argument_list|,
name|description
operator|=
literal|"Timeout for requests issued from the standby instance in milliseconds"
argument_list|)
name|int
name|standby_readtimeout
parameter_list|()
default|default
literal|60000
function_decl|;
annotation|@
name|AttributeDefinition
argument_list|(
name|name
operator|=
literal|"Standby Automatic Cleanup"
argument_list|,
name|description
operator|=
literal|"Call the cleanup method when the root segment Garbage Collector (GC) generation number increases"
argument_list|)
name|boolean
name|standby_autoclean
parameter_list|()
default|default
literal|true
function_decl|;
block|}
annotation|@
name|Reference
argument_list|(
name|policy
operator|=
name|STATIC
argument_list|,
name|policyOption
operator|=
name|GREEDY
argument_list|)
specifier|private
name|SegmentStoreProvider
name|storeProvider
init|=
literal|null
decl_stmt|;
specifier|private
specifier|final
name|Closer
name|closer
init|=
name|Closer
operator|.
name|create
argument_list|()
decl_stmt|;
annotation|@
name|Activate
specifier|private
name|void
name|activate
parameter_list|(
name|ComponentContext
name|context
parameter_list|,
name|Configuration
name|config
parameter_list|)
block|{
name|SegmentStore
name|segmentStore
init|=
name|storeProvider
operator|.
name|getSegmentStore
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|segmentStore
operator|instanceof
name|FileStore
operator|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unexpected SegmentStore implementation"
argument_list|)
throw|;
block|}
name|FileStore
name|fileStore
init|=
operator|(
name|FileStore
operator|)
name|segmentStore
decl_stmt|;
name|String
name|mode
init|=
name|config
operator|.
name|mode
argument_list|()
decl_stmt|;
if|if
condition|(
name|mode
operator|.
name|equals
argument_list|(
literal|"primary"
argument_list|)
condition|)
block|{
name|bootstrapMaster
argument_list|(
name|config
argument_list|,
name|fileStore
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|mode
operator|.
name|equals
argument_list|(
literal|"standby"
argument_list|)
condition|)
block|{
name|bootstrapSlave
argument_list|(
name|context
argument_list|,
name|config
argument_list|,
name|fileStore
argument_list|)
expr_stmt|;
return|return;
block|}
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Unexpected mode property, got '%s'"
argument_list|,
name|mode
argument_list|)
argument_list|)
throw|;
block|}
annotation|@
name|Deactivate
specifier|public
name|void
name|deactivate
parameter_list|()
throws|throws
name|Exception
block|{
name|closer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|bootstrapMaster
parameter_list|(
name|Configuration
name|config
parameter_list|,
name|FileStore
name|fileStore
parameter_list|)
block|{
name|int
name|port
init|=
name|config
operator|.
name|port
argument_list|()
decl_stmt|;
name|String
index|[]
name|ranges
init|=
name|config
operator|.
name|primary_allowed$_$client$_$ip$_$ranges
argument_list|()
decl_stmt|;
name|boolean
name|secure
init|=
name|config
operator|.
name|secure
argument_list|()
decl_stmt|;
name|StandbyServerSync
name|standbyServerSync
init|=
operator|new
name|StandbyServerSync
argument_list|(
name|port
argument_list|,
name|fileStore
argument_list|,
name|BLOB_CHUNK_SIZE
argument_list|,
name|ranges
argument_list|,
name|secure
argument_list|)
decl_stmt|;
name|closer
operator|.
name|register
argument_list|(
name|standbyServerSync
argument_list|)
expr_stmt|;
name|standbyServerSync
operator|.
name|start
argument_list|()
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Started primary on port {} with allowed IP ranges {}"
argument_list|,
name|port
argument_list|,
name|ranges
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|bootstrapSlave
parameter_list|(
name|ComponentContext
name|context
parameter_list|,
name|Configuration
name|config
parameter_list|,
name|FileStore
name|fileStore
parameter_list|)
block|{
name|int
name|port
init|=
name|config
operator|.
name|port
argument_list|()
decl_stmt|;
name|long
name|interval
init|=
name|config
operator|.
name|interval
argument_list|()
decl_stmt|;
name|String
name|host
init|=
name|config
operator|.
name|primary_host
argument_list|()
decl_stmt|;
name|boolean
name|secure
init|=
name|config
operator|.
name|secure
argument_list|()
decl_stmt|;
name|int
name|readTimeout
init|=
name|config
operator|.
name|standby_readtimeout
argument_list|()
decl_stmt|;
name|boolean
name|clean
init|=
name|config
operator|.
name|standby_autoclean
argument_list|()
decl_stmt|;
name|StandbyClientSync
name|standbyClientSync
init|=
operator|new
name|StandbyClientSync
argument_list|(
name|host
argument_list|,
name|port
argument_list|,
name|fileStore
argument_list|,
name|secure
argument_list|,
name|readTimeout
argument_list|,
name|clean
argument_list|,
operator|new
name|File
argument_list|(
name|StandardSystemProperty
operator|.
name|JAVA_IO_TMPDIR
operator|.
name|value
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|closer
operator|.
name|register
argument_list|(
name|standbyClientSync
argument_list|)
expr_stmt|;
name|Dictionary
argument_list|<
name|Object
argument_list|,
name|Object
argument_list|>
name|dictionary
init|=
operator|new
name|Hashtable
argument_list|<
name|Object
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|dictionary
operator|.
name|put
argument_list|(
literal|"scheduler.period"
argument_list|,
name|interval
argument_list|)
expr_stmt|;
name|dictionary
operator|.
name|put
argument_list|(
literal|"scheduler.concurrent"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|ServiceRegistration
name|registration
init|=
name|context
operator|.
name|getBundleContext
argument_list|()
operator|.
name|registerService
argument_list|(
name|Runnable
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|standbyClientSync
argument_list|,
name|dictionary
argument_list|)
decl_stmt|;
name|closer
operator|.
name|register
argument_list|(
name|registration
operator|::
name|unregister
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Started standby on port {} with {}s sync frequency"
argument_list|,
name|port
argument_list|,
name|interval
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

