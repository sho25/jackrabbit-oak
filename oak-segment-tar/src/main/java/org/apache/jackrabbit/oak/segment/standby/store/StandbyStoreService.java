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
name|java
operator|.
name|lang
operator|.
name|String
operator|.
name|valueOf
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
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
name|apache
operator|.
name|felix
operator|.
name|scr
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
name|Closeable
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
name|felix
operator|.
name|scr
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
name|apache
operator|.
name|felix
operator|.
name|scr
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
name|apache
operator|.
name|felix
operator|.
name|scr
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
name|apache
operator|.
name|felix
operator|.
name|scr
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
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|Property
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|PropertyOption
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
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
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|commons
operator|.
name|PropertiesUtil
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
name|Property
argument_list|(
name|name
operator|=
literal|"org.apache.sling.installer.configuration.persist"
argument_list|,
name|label
operator|=
literal|"Persist configuration"
argument_list|,
name|description
operator|=
literal|"Must be always disabled to avoid storing the configuration in the repository"
argument_list|,
name|boolValue
operator|=
literal|false
argument_list|)
annotation|@
name|Component
argument_list|(
name|metatype
operator|=
literal|true
argument_list|,
name|policy
operator|=
name|ConfigurationPolicy
operator|.
name|REQUIRE
argument_list|)
specifier|public
class|class
name|StandbyStoreService
block|{
specifier|private
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|getClass
argument_list|()
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|MODE_PRIMARY
init|=
literal|"primary"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|MODE_STANDBY
init|=
literal|"standby"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|MODE_DEFAULT
init|=
name|MODE_PRIMARY
decl_stmt|;
annotation|@
name|Property
argument_list|(
name|options
operator|=
block|{
annotation|@
name|PropertyOption
argument_list|(
name|name
operator|=
name|MODE_PRIMARY
argument_list|,
name|value
operator|=
name|MODE_PRIMARY
argument_list|)
block|,
annotation|@
name|PropertyOption
argument_list|(
name|name
operator|=
name|MODE_STANDBY
argument_list|,
name|value
operator|=
name|MODE_STANDBY
argument_list|)
block|}
argument_list|,
name|value
operator|=
name|MODE_DEFAULT
argument_list|)
specifier|public
specifier|static
specifier|final
name|String
name|MODE
init|=
literal|"mode"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|PORT_DEFAULT
init|=
literal|8023
decl_stmt|;
annotation|@
name|Property
argument_list|(
name|intValue
operator|=
name|PORT_DEFAULT
argument_list|)
specifier|public
specifier|static
specifier|final
name|String
name|PORT
init|=
literal|"port"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|PRIMARY_HOST_DEFAULT
init|=
literal|"127.0.0.1"
decl_stmt|;
annotation|@
name|Property
argument_list|(
name|value
operator|=
name|PRIMARY_HOST_DEFAULT
argument_list|)
specifier|public
specifier|static
specifier|final
name|String
name|PRIMARY_HOST
init|=
literal|"primary.host"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|INTERVAL_DEFAULT
init|=
literal|5
decl_stmt|;
annotation|@
name|Property
argument_list|(
name|intValue
operator|=
name|INTERVAL_DEFAULT
argument_list|)
specifier|public
specifier|static
specifier|final
name|String
name|INTERVAL
init|=
literal|"interval"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
index|[]
name|ALLOWED_CLIENT_IP_RANGES_DEFAULT
init|=
operator|new
name|String
index|[]
block|{}
decl_stmt|;
annotation|@
name|Property
argument_list|(
name|cardinality
operator|=
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
specifier|public
specifier|static
specifier|final
name|String
name|ALLOWED_CLIENT_IP_RANGES
init|=
literal|"primary.allowed-client-ip-ranges"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|boolean
name|SECURE_DEFAULT
init|=
literal|false
decl_stmt|;
annotation|@
name|Property
argument_list|(
name|boolValue
operator|=
name|SECURE_DEFAULT
argument_list|)
specifier|public
specifier|static
specifier|final
name|String
name|SECURE
init|=
literal|"secure"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|READ_TIMEOUT_DEFAULT
init|=
literal|60000
decl_stmt|;
annotation|@
name|Property
argument_list|(
name|intValue
operator|=
name|READ_TIMEOUT_DEFAULT
argument_list|)
specifier|public
specifier|static
specifier|final
name|String
name|READ_TIMEOUT
init|=
literal|"standby.readtimeout"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|boolean
name|AUTO_CLEAN_DEFAULT
init|=
literal|true
decl_stmt|;
annotation|@
name|Property
argument_list|(
name|boolValue
operator|=
name|AUTO_CLEAN_DEFAULT
argument_list|)
specifier|public
specifier|static
specifier|final
name|String
name|AUTO_CLEAN
init|=
literal|"standby.autoclean"
decl_stmt|;
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
name|valueOf
argument_list|(
name|context
operator|.
name|getProperties
argument_list|()
operator|.
name|get
argument_list|(
name|MODE
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|MODE_PRIMARY
operator|.
name|equals
argument_list|(
name|mode
argument_list|)
condition|)
block|{
name|bootstrapMaster
argument_list|(
name|context
argument_list|,
name|fileStore
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|MODE_STANDBY
operator|.
name|equals
argument_list|(
name|mode
argument_list|)
condition|)
block|{
name|bootstrapSlave
argument_list|(
name|context
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
name|ComponentContext
name|context
parameter_list|,
name|FileStore
name|fileStore
parameter_list|)
block|{
name|Dictionary
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
name|props
init|=
name|context
operator|.
name|getProperties
argument_list|()
decl_stmt|;
name|int
name|port
init|=
name|PropertiesUtil
operator|.
name|toInteger
argument_list|(
name|props
operator|.
name|get
argument_list|(
name|PORT
argument_list|)
argument_list|,
name|PORT_DEFAULT
argument_list|)
decl_stmt|;
name|String
index|[]
name|ranges
init|=
name|PropertiesUtil
operator|.
name|toStringArray
argument_list|(
name|props
operator|.
name|get
argument_list|(
name|ALLOWED_CLIENT_IP_RANGES
argument_list|)
argument_list|,
name|ALLOWED_CLIENT_IP_RANGES_DEFAULT
argument_list|)
decl_stmt|;
name|boolean
name|secure
init|=
name|PropertiesUtil
operator|.
name|toBoolean
argument_list|(
name|props
operator|.
name|get
argument_list|(
name|SECURE
argument_list|)
argument_list|,
name|SECURE_DEFAULT
argument_list|)
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
name|FileStore
name|fileStore
parameter_list|)
block|{
name|Dictionary
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
name|props
init|=
name|context
operator|.
name|getProperties
argument_list|()
decl_stmt|;
name|int
name|port
init|=
name|PropertiesUtil
operator|.
name|toInteger
argument_list|(
name|props
operator|.
name|get
argument_list|(
name|PORT
argument_list|)
argument_list|,
name|PORT_DEFAULT
argument_list|)
decl_stmt|;
name|long
name|interval
init|=
name|PropertiesUtil
operator|.
name|toInteger
argument_list|(
name|props
operator|.
name|get
argument_list|(
name|INTERVAL
argument_list|)
argument_list|,
name|INTERVAL_DEFAULT
argument_list|)
decl_stmt|;
name|String
name|host
init|=
name|PropertiesUtil
operator|.
name|toString
argument_list|(
name|props
operator|.
name|get
argument_list|(
name|PRIMARY_HOST
argument_list|)
argument_list|,
name|PRIMARY_HOST_DEFAULT
argument_list|)
decl_stmt|;
name|boolean
name|secure
init|=
name|PropertiesUtil
operator|.
name|toBoolean
argument_list|(
name|props
operator|.
name|get
argument_list|(
name|SECURE
argument_list|)
argument_list|,
name|SECURE_DEFAULT
argument_list|)
decl_stmt|;
name|int
name|readTimeout
init|=
name|PropertiesUtil
operator|.
name|toInteger
argument_list|(
name|props
operator|.
name|get
argument_list|(
name|READ_TIMEOUT
argument_list|)
argument_list|,
name|READ_TIMEOUT_DEFAULT
argument_list|)
decl_stmt|;
name|boolean
name|clean
init|=
name|PropertiesUtil
operator|.
name|toBoolean
argument_list|(
name|props
operator|.
name|get
argument_list|(
name|AUTO_CLEAN
argument_list|)
argument_list|,
name|AUTO_CLEAN_DEFAULT
argument_list|)
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
name|asCloseable
argument_list|(
name|registration
argument_list|)
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
specifier|private
specifier|static
name|Closeable
name|asCloseable
parameter_list|(
specifier|final
name|ServiceRegistration
name|r
parameter_list|)
block|{
return|return
operator|new
name|Closeable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
block|{
name|r
operator|.
name|unregister
argument_list|()
expr_stmt|;
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

