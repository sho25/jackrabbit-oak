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
name|api
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
comment|/**  * {@code AuthInfo} instances provide access to information related  * to authentication and authorization of a given content session.  * {@code AuthInfo} instances are guaranteed to be immutable.  */
end_comment

begin_interface
specifier|public
interface|interface
name|AuthInfo
block|{
name|AuthInfo
name|EMPTY
init|=
operator|new
name|AuthInfo
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|getUserID
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
index|[]
name|getAttributeNames
parameter_list|()
block|{
return|return
operator|new
name|String
index|[
literal|0
index|]
return|;
block|}
annotation|@
name|Override
specifier|public
name|Object
name|getAttribute
parameter_list|(
name|String
name|attributeName
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
decl_stmt|;
comment|/**      * Return the user ID to be exposed on the JCR Session object. It refers      * to the ID of the user associated with the Credentials passed to the      * repository login.      *      * @return the user ID such as exposed on the JCR Session object.      */
annotation|@
name|CheckForNull
name|String
name|getUserID
parameter_list|()
function_decl|;
comment|/**      * Returns the attribute names associated with this instance.      *      * @return The attribute names with that instance or an empty array if      * no attributes are present.      */
annotation|@
name|Nonnull
name|String
index|[]
name|getAttributeNames
parameter_list|()
function_decl|;
comment|/**      * Returns the attribute with the given name or {@code null} if no attribute      * with that {@code attributeName} exists.      *      * @param attributeName The attribute name.      * @return The attribute or {@code null}.      */
annotation|@
name|CheckForNull
name|Object
name|getAttribute
parameter_list|(
name|String
name|attributeName
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

