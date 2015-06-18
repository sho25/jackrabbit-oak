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
name|oak
operator|.
name|plugins
operator|.
name|blob
operator|.
name|datastore
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
name|jackrabbit
operator|.
name|core
operator|.
name|data
operator|.
name|CachingFDS
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
name|core
operator|.
name|data
operator|.
name|DataStore
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

begin_comment
comment|/**  * OSGI server wrapper over {@link CachingFDS}  *  */
end_comment

begin_class
annotation|@
name|Component
argument_list|(
name|policy
operator|=
name|ConfigurationPolicy
operator|.
name|REQUIRE
argument_list|,
name|name
operator|=
name|CachingFDSService
operator|.
name|NAME
argument_list|)
specifier|public
class|class
name|CachingFDSService
extends|extends
name|AbstractDataStoreService
block|{
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"org.apache.jackrabbit.oak.plugins.blob.datastore.CachingFDS"
decl_stmt|;
annotation|@
name|Override
specifier|protected
name|DataStore
name|createDataStore
parameter_list|(
name|ComponentContext
name|context
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|config
parameter_list|)
block|{
name|CachingFDS
name|dataStore
init|=
operator|new
name|CachingFDS
argument_list|()
decl_stmt|;
name|Properties
name|properties
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|properties
operator|.
name|putAll
argument_list|(
name|config
argument_list|)
expr_stmt|;
name|dataStore
operator|.
name|setProperties
argument_list|(
name|properties
argument_list|)
expr_stmt|;
return|return
name|dataStore
return|;
block|}
annotation|@
name|Override
specifier|protected
name|String
index|[]
name|getDescription
parameter_list|()
block|{
return|return
operator|new
name|String
index|[]
block|{
literal|"type=cachingFDS"
block|}
return|;
block|}
block|}
end_class

end_unit

