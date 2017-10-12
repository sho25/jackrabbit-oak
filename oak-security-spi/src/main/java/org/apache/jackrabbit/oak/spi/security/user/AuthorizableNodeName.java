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
name|spi
operator|.
name|security
operator|.
name|user
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
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
name|util
operator|.
name|Text
import|;
end_import

begin_comment
comment|/**  * The {@code AuthorizableNodeName} is in charge of generating a valid node  * name from a given authorizable ID.  *  * @since OAK 1.0  */
end_comment

begin_interface
specifier|public
interface|interface
name|AuthorizableNodeName
block|{
comment|/**      * Default {@code AuthorizableNodeName} instance.      *      * @see AuthorizableNodeName.Default      */
name|AuthorizableNodeName
name|DEFAULT
init|=
operator|new
name|Default
argument_list|()
decl_stmt|;
comment|/**      * Generates a node name from the specified {@code authorizableId}.      *      * @param authorizableId The ID of the authorizable to be created.      * @return A valid node name.      */
annotation|@
name|Nonnull
name|String
name|generateNodeName
parameter_list|(
annotation|@
name|Nonnull
name|String
name|authorizableId
parameter_list|)
function_decl|;
comment|/**      * Default implementation of the {@code AuthorizableNodeName} interface      * that uses the specified authorizable identifier as node name      * {@link org.apache.jackrabbit.util.Text#escapeIllegalJcrChars(String) escaping}      * any illegal JCR chars.      */
specifier|final
class|class
name|Default
implements|implements
name|AuthorizableNodeName
block|{
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|String
name|generateNodeName
parameter_list|(
annotation|@
name|Nonnull
name|String
name|authorizableId
parameter_list|)
block|{
return|return
name|Text
operator|.
name|escapeIllegalJcrChars
argument_list|(
name|authorizableId
argument_list|)
return|;
block|}
block|}
block|}
end_interface

end_unit

