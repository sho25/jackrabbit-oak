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
name|authorization
operator|.
name|restriction
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
name|oak
operator|.
name|api
operator|.
name|Type
import|;
end_import

begin_comment
comment|/**  * The {@code RestrictionDefinition} interface provides methods for  * discovering the static definition of any additional policy-internal refinements  * of the access control definitions. These restrictions are intended to be  * used wherever effects are too fine-grained to be exposed through privilege  * discovery or define a different restriction mechanism. A common case may be  * to provide finer-grained access restrictions to individual properties or  * child nodes of the node to which the policy applies e.g. by means of  * naming patterns or node type restrictions.  *  * Its subclass {@code Restriction} adds methods that are relevant only when  * a given restriction is "live" after being created and applied to a given  * policy.  *  * @see org.apache.jackrabbit.api.security.JackrabbitAccessControlList#getRestrictionNames()  * @see org.apache.jackrabbit.api.security.JackrabbitAccessControlList#getRestrictionType(String)  */
end_comment

begin_interface
specifier|public
interface|interface
name|RestrictionDefinition
block|{
comment|/**      * The internal oak name of this restriction definition.      *      * @return The oak name.      */
annotation|@
name|Nonnull
name|String
name|getName
parameter_list|()
function_decl|;
comment|/**      * The required type as defined by this definition.      *      * @return The required type which must be a valid {@link javax.jcr.PropertyType}.      */
annotation|@
name|Nonnull
name|Type
argument_list|<
name|?
argument_list|>
name|getRequiredType
parameter_list|()
function_decl|;
comment|/**      * Indicates if this restriction is mandatory.      *      * @return {@code true} if this restriction is mandatory; {@code false} otherwise.      */
name|boolean
name|isMandatory
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

