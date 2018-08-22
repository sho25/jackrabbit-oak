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
name|jcr
operator|.
name|binary
operator|.
name|fixtures
operator|.
name|datastore
package|;
end_package

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
name|jetbrains
operator|.
name|annotations
operator|.
name|NotNull
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|Nullable
import|;
end_import

begin_comment
comment|/**  *  DataStore fixture for parametrized tests. To be used inside NodeStoreFixture implementations.  */
end_comment

begin_interface
specifier|public
interface|interface
name|DataStoreFixture
block|{
comment|/**      * Create a new DataStore instance. This might include setting up a temporary folder,      * bucket, container... of the underlying storage solution. Return null if this      * DataStore is not available because of missing configuration or setup issues      * (implementation shall log such warnings/errors).      *      * Calling DataStore.init() is left to the client.      */
annotation|@
name|NotNull
name|DataStore
name|createDataStore
parameter_list|()
function_decl|;
comment|/**      * Dispose a DataStore. This can include removing the temporary test folder, bucket etc.      */
name|void
name|dispose
parameter_list|(
name|DataStore
name|dataStore
parameter_list|)
function_decl|;
comment|/**      * Return whether this fixture is available, for example if the necessary configuration is present.      */
name|boolean
name|isAvailable
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

