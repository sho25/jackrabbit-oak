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
name|plugins
operator|.
name|segment
operator|.
name|failover
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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|cert
operator|.
name|CertificateException
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
name|javax
operator|.
name|net
operator|.
name|ssl
operator|.
name|SSLException
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
name|plugins
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
name|plugins
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
name|plugins
operator|.
name|segment
operator|.
name|failover
operator|.
name|client
operator|.
name|FailoverClient
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
name|plugins
operator|.
name|segment
operator|.
name|failover
operator|.
name|server
operator|.
name|FailoverServer
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
name|spi
operator|.
name|state
operator|.
name|NodeStore
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
name|Component
argument_list|(
name|policy
operator|=
name|ConfigurationPolicy
operator|.
name|REQUIRE
argument_list|)
specifier|public
class|class
name|FailoverStoreService
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
name|MODE_MASTER
init|=
literal|"master"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|MODE_SLAVE
init|=
literal|"slave"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|MODE_DEFAULT
init|=
name|MODE_MASTER
decl_stmt|;
annotation|@
name|Property
argument_list|(
name|label
operator|=
literal|"Mode"
argument_list|,
name|description
operator|=
literal|"TarMK Failover mode (master or slave)"
argument_list|,
name|options
operator|=
block|{
annotation|@
name|PropertyOption
argument_list|(
name|name
operator|=
literal|"master"
argument_list|,
name|value
operator|=
literal|"master"
argument_list|)
block|,
annotation|@
name|PropertyOption
argument_list|(
name|name
operator|=
literal|"slave"
argument_list|,
name|value
operator|=
literal|"slave"
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
name|label
operator|=
literal|"Port"
argument_list|,
name|description
operator|=
literal|"TarMK Failover port"
argument_list|,
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
name|MASTER_HOST_DEFAULT
init|=
literal|"127.0.0.1"
decl_stmt|;
annotation|@
name|Property
argument_list|(
name|label
operator|=
literal|"Master Host"
argument_list|,
name|description
operator|=
literal|"TarMK Failover master host (enabled for slave mode only)"
argument_list|,
name|value
operator|=
name|MASTER_HOST_DEFAULT
argument_list|)
specifier|public
specifier|static
specifier|final
name|String
name|MASTER_HOST
init|=
literal|"master.host"
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
name|label
operator|=
literal|"Sync interval (seconds)"
argument_list|,
name|description
operator|=
literal|"TarMK Failover sync interval (seconds)"
argument_list|,
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
name|ALLOWED_CLIENT_IP_RANGES_DEFAULT
init|=
literal|null
decl_stmt|;
annotation|@
name|Property
argument_list|(
name|label
operator|=
literal|"Client allowed IP-Ranges"
argument_list|,
name|description
operator|=
literal|"accept incoming requests for these IP-ranges only"
argument_list|)
specifier|public
specifier|static
specifier|final
name|String
name|ALLOWED_CLIENT_IP_RANGES
init|=
literal|"master.allowed-client-ip-ranges"
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
name|label
operator|=
literal|"Secure"
argument_list|,
name|description
operator|=
literal|"Use secure connections"
argument_list|,
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
name|SegmentStore
name|segmentStore
decl_stmt|;
specifier|private
name|FailoverServer
name|master
init|=
literal|null
decl_stmt|;
specifier|private
name|FailoverClient
name|sync
init|=
literal|null
decl_stmt|;
specifier|private
name|ServiceRegistration
name|syncReg
init|=
literal|null
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
throws|throws
name|IOException
throws|,
name|CertificateException
block|{
if|if
condition|(
name|storeProvider
operator|!=
literal|null
condition|)
block|{
name|segmentStore
operator|=
name|storeProvider
operator|.
name|getSegmentStore
argument_list|()
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Missing SegmentStoreProvider service"
argument_list|)
throw|;
block|}
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
name|MODE_MASTER
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
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|MODE_SLAVE
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
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unexpected 'mode' param, expecting 'master' or 'slave' got "
operator|+
name|mode
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Deactivate
specifier|public
specifier|synchronized
name|void
name|deactivate
parameter_list|()
block|{
if|if
condition|(
name|master
operator|!=
literal|null
condition|)
block|{
name|master
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|sync
operator|!=
literal|null
condition|)
block|{
name|sync
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|syncReg
operator|!=
literal|null
condition|)
block|{
name|syncReg
operator|.
name|unregister
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|bootstrapMaster
parameter_list|(
name|ComponentContext
name|context
parameter_list|)
throws|throws
name|CertificateException
throws|,
name|SSLException
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
name|ipRanges
init|=
name|PropertiesUtil
operator|.
name|toString
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
name|String
index|[]
name|ranges
init|=
name|ipRanges
operator|==
literal|null
condition|?
literal|null
else|:
name|ipRanges
operator|.
name|split
argument_list|(
literal|","
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
name|master
operator|=
operator|new
name|FailoverServer
argument_list|(
name|port
argument_list|,
name|segmentStore
argument_list|,
name|ranges
argument_list|,
name|secure
argument_list|)
expr_stmt|;
name|master
operator|.
name|start
argument_list|()
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"started failover master on port {} with allowed ip ranges {}."
argument_list|,
name|port
argument_list|,
name|ipRanges
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|bootstrapSlave
parameter_list|(
name|ComponentContext
name|context
parameter_list|)
throws|throws
name|SSLException
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
name|MASTER_HOST
argument_list|)
argument_list|,
name|MASTER_HOST_DEFAULT
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
name|sync
operator|=
operator|new
name|FailoverClient
argument_list|(
name|host
argument_list|,
name|port
argument_list|,
name|segmentStore
argument_list|,
name|secure
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
name|dictionary
operator|.
name|put
argument_list|(
literal|"scheduler.runOn"
argument_list|,
literal|"SINGLE"
argument_list|)
expr_stmt|;
name|syncReg
operator|=
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
name|sync
argument_list|,
name|dictionary
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"started failover slave sync with {}:{} at {} sec."
argument_list|,
name|host
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

