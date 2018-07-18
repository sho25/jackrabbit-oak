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
name|namepath
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
name|javax
operator|.
name|jcr
operator|.
name|RepositoryException
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

begin_comment
comment|/**  * TODO document  */
end_comment

begin_interface
annotation|@
name|ProviderType
specifier|public
interface|interface
name|NameMapper
block|{
comment|/**      * Returns the Oak name for the given JCR name, or {@code null} if no      * such mapping exists because the given JCR name contains an unknown      * namespace URI or prefix, or is otherwise invalid.      *      * @param jcrName JCR name      * @return Oak name, or {@code null}      */
annotation|@
name|Nullable
name|String
name|getOakNameOrNull
parameter_list|(
annotation|@
name|NotNull
name|String
name|jcrName
parameter_list|)
function_decl|;
comment|/**      * Returns the Oak name for the specified JCR name. In contrast to      * {@link #getOakNameOrNull(String)} this method will throw a {@code RepositoryException}      * if the JCR name is invalid and cannot be resolved.      *      * @param jcrName The JCR name to be converted.      * @return A valid Oak name.      * @throws RepositoryException If the JCR name cannot be resolved.      */
annotation|@
name|NotNull
name|String
name|getOakName
parameter_list|(
annotation|@
name|NotNull
name|String
name|jcrName
parameter_list|)
throws|throws
name|RepositoryException
function_decl|;
comment|/**      * Returns the local namespace prefix mappings, or an empty map if      * there aren't any local mappings.      *      * @return local namespace prefix to URI mappings      */
annotation|@
name|NotNull
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getSessionLocalMappings
parameter_list|()
function_decl|;
comment|/**      * Returns the JCR name for the given Oak name. The given name is      * expected to have come from a valid Oak repository that contains      * only valid names with proper namespace mappings. If that's not      * the case, either a programming error or a repository corruption      * has occurred and an appropriate unchecked exception gets thrown.      *      * @param oakName Oak name      * @return JCR name      */
annotation|@
name|NotNull
name|String
name|getJcrName
parameter_list|(
annotation|@
name|NotNull
name|String
name|oakName
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

