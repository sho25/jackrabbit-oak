begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|mongomk
operator|.
name|osgi
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
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
name|Property
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
name|mk
operator|.
name|api
operator|.
name|MicroKernel
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
name|mongomk
operator|.
name|MongoMK
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
name|mongomk
operator|.
name|util
operator|.
name|MongoConnection
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|sling
operator|.
name|commons
operator|.
name|osgi
operator|.
name|PropertiesUtil
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
name|BundleContext
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
name|annotations
operator|.
name|Deactivate
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

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|DB
import|;
end_import

begin_comment
comment|/**  * The OSGi service to start/stop a MongoMK instance.  */
end_comment

begin_class
annotation|@
name|Component
argument_list|(
name|metatype
operator|=
literal|true
argument_list|,
name|label
operator|=
literal|"%oak.mongomk.label"
argument_list|,
name|description
operator|=
literal|"%oak.mongomk.description"
argument_list|,
name|policy
operator|=
name|ConfigurationPolicy
operator|.
name|REQUIRE
argument_list|)
specifier|public
class|class
name|MongoMicroKernelService
block|{
specifier|private
specifier|static
specifier|final
name|String
name|DEFAULT_HOST
init|=
literal|"localhost"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULT_PORT
init|=
literal|27017
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|DEFAULT_DB
init|=
literal|"oak"
decl_stmt|;
annotation|@
name|Property
argument_list|(
name|value
operator|=
name|DEFAULT_HOST
argument_list|)
specifier|private
specifier|static
specifier|final
name|String
name|PROP_HOST
init|=
literal|"host"
decl_stmt|;
annotation|@
name|Property
argument_list|(
name|intValue
operator|=
name|DEFAULT_PORT
argument_list|)
specifier|private
specifier|static
specifier|final
name|String
name|PROP_PORT
init|=
literal|"port"
decl_stmt|;
annotation|@
name|Property
argument_list|(
name|value
operator|=
name|DEFAULT_DB
argument_list|)
specifier|private
specifier|static
specifier|final
name|String
name|PROP_DB
init|=
literal|"db"
decl_stmt|;
specifier|private
specifier|final
name|Logger
name|logger
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|this
operator|.
name|getClass
argument_list|()
argument_list|)
decl_stmt|;
specifier|private
name|ServiceRegistration
name|reg
decl_stmt|;
specifier|private
name|MongoMK
name|mk
decl_stmt|;
annotation|@
name|Activate
specifier|private
name|void
name|activate
parameter_list|(
name|BundleContext
name|context
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|?
argument_list|>
name|config
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|host
init|=
name|PropertiesUtil
operator|.
name|toString
argument_list|(
name|config
operator|.
name|get
argument_list|(
name|PROP_HOST
argument_list|)
argument_list|,
name|DEFAULT_HOST
argument_list|)
decl_stmt|;
name|int
name|port
init|=
name|PropertiesUtil
operator|.
name|toInteger
argument_list|(
name|config
operator|.
name|get
argument_list|(
name|PROP_PORT
argument_list|)
argument_list|,
name|DEFAULT_PORT
argument_list|)
decl_stmt|;
name|String
name|db
init|=
name|PropertiesUtil
operator|.
name|toString
argument_list|(
name|config
operator|.
name|get
argument_list|(
name|PROP_DB
argument_list|)
argument_list|,
name|DEFAULT_DB
argument_list|)
decl_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Starting MongoDB MicroKernel with host={}, port={}, db={}"
argument_list|,
operator|new
name|Object
index|[]
block|{
name|host
block|,
name|port
block|,
name|db
block|}
argument_list|)
expr_stmt|;
name|MongoConnection
name|connection
init|=
operator|new
name|MongoConnection
argument_list|(
name|host
argument_list|,
name|port
argument_list|,
name|db
argument_list|)
decl_stmt|;
name|DB
name|mongoDB
init|=
name|connection
operator|.
name|getDB
argument_list|()
decl_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Connected to database {}"
argument_list|,
name|mongoDB
argument_list|)
expr_stmt|;
name|mk
operator|=
operator|new
name|MongoMK
operator|.
name|Builder
argument_list|()
operator|.
name|setMongoDB
argument_list|(
name|mongoDB
argument_list|)
operator|.
name|open
argument_list|()
expr_stmt|;
name|Properties
name|props
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|props
operator|.
name|setProperty
argument_list|(
literal|"oak.mk.type"
argument_list|,
literal|"mongo"
argument_list|)
expr_stmt|;
name|reg
operator|=
name|context
operator|.
name|registerService
argument_list|(
name|MicroKernel
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|mk
argument_list|,
name|props
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Deactivate
specifier|private
name|void
name|deactivate
parameter_list|()
block|{
if|if
condition|(
name|reg
operator|!=
literal|null
condition|)
block|{
name|reg
operator|.
name|unregister
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|mk
operator|!=
literal|null
condition|)
block|{
name|mk
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

