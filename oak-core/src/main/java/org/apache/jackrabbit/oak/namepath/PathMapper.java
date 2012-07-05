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
name|javax
operator|.
name|annotation
operator|.
name|CheckForNull
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
import|;
end_import

begin_comment
comment|/**  * {@code PathMapper} instances provide methods for mapping paths from their JCR  * string representation to their Oak representation and vice versa.  */
end_comment

begin_interface
specifier|public
interface|interface
name|PathMapper
block|{
comment|/**      * Returns the Oak path for the given JCR path, or {@code null} if no      * such mapping exists because the given JCR path contains a name element      * with an unknown namespace URI or prefix, or is otherwise invalid.      *      * @param jcrPath JCR path      * @return Oak path, or {@code null}      */
annotation|@
name|CheckForNull
name|String
name|getOakPath
parameter_list|(
name|String
name|jcrPath
parameter_list|)
function_decl|;
comment|/**      * As {@link #getOakPath(String)}, but preserving the index information      *      * @param jcrPath JCR path      * @return mapped path, or {@code null}      */
annotation|@
name|CheckForNull
name|String
name|getOakPathKeepIndex
parameter_list|(
name|String
name|jcrPath
parameter_list|)
function_decl|;
comment|/**      * Returns the JCR path for the given Oak path. The given path is      * expected to have come from a valid Oak repository that contains      * only valid paths whose name elements only use proper namespace      * mappings. If that's not the case, either a programming error or      * a repository corruption has occurred and an appropriate unchecked      * exception gets thrown.      *      * @param oakPath Oak path      * @return JCR path      */
annotation|@
name|Nonnull
name|String
name|getJcrPath
parameter_list|(
name|String
name|oakPath
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

