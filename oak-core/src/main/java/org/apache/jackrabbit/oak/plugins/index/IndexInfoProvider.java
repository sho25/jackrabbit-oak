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
name|index
package|;
end_package

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
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|Nullable
import|;
end_import

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
name|ConsumerType
import|;
end_import

begin_comment
comment|/**  * Service to be provided by various index implementations.  * This would then be used by {@code IndexInfoService}  * for collecting information related to index.  */
end_comment

begin_interface
annotation|@
name|ConsumerType
specifier|public
interface|interface
name|IndexInfoProvider
block|{
comment|/**      * Index type for this implementation can provide information      */
name|String
name|getType
parameter_list|()
function_decl|;
annotation|@
name|Nullable
name|IndexInfo
name|getInfo
parameter_list|(
name|String
name|indexPath
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Determined if the index is valid and usable. If the index is corrupt      * then it returns false      */
name|boolean
name|isValid
parameter_list|(
name|String
name|indexPath
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

