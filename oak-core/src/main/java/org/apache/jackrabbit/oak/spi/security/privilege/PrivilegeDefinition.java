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
name|privilege
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
comment|/**  * The {@code PrivilegeDefinition} interface defines the characteristics of  * a JCR {@link javax.jcr.security.Privilege}.  */
end_comment

begin_interface
specifier|public
interface|interface
name|PrivilegeDefinition
block|{
comment|/**      * The internal name of this privilege.      *      * @return the internal name.      */
annotation|@
name|Nonnull
name|String
name|getName
parameter_list|()
function_decl|;
comment|/**      * Returns {@code true} if the privilege described by this definition      * is abstract.      *      * @return {@code true} if the resulting privilege is abstract;      * {@code false} otherwise.      */
name|boolean
name|isAbstract
parameter_list|()
function_decl|;
comment|/**      * Returns the internal names of the declared aggregated privileges or      * an empty array if the privilege defined by this definition isn't      * an aggregate.      *      * @return The internal names of the aggregated privileges or an empty array.      */
annotation|@
name|Nonnull
name|Set
argument_list|<
name|String
argument_list|>
name|getDeclaredAggregateNames
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

