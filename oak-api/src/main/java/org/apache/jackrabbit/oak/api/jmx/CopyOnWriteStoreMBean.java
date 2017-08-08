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
name|api
operator|.
name|jmx
package|;
end_package

begin_import
import|import
name|org
operator|.
name|osgi
operator|.
name|annotation
operator|.
name|versioning
operator|.
name|ProviderType
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|TabularData
import|;
end_import

begin_comment
comment|/**  * MBean for managing the copy-on-write node store  */
end_comment

begin_interface
annotation|@
name|ProviderType
specifier|public
interface|interface
name|CopyOnWriteStoreMBean
block|{
name|String
name|TYPE
init|=
literal|"CopyOnWriteStoreManager"
decl_stmt|;
comment|/**      * Enabled the temporary, copy-on-write store      * @return the operation status      */
name|String
name|enableCopyOnWrite
parameter_list|()
function_decl|;
comment|/**      * Disables the temporary store and switched the repository back to the "normal" mode.      * @return the operation status      */
name|String
name|disableCopyOnWrite
parameter_list|()
function_decl|;
comment|/**      * Returns the copy-on-write status      * @return status of the copy-on-write mode      */
name|String
name|getStatus
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

