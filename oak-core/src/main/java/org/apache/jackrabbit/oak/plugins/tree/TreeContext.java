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
name|plugins
operator|.
name|tree
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
name|PropertyState
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
name|Tree
import|;
end_import

begin_comment
comment|/**  * {@code TreeContext} represents item related information in relation to a  * dedicated module.  * This information allows to determine if a given {@code Tree} or {@link PropertyState}  * is defined by or related to the model provided by a given setup.  */
end_comment

begin_interface
specifier|public
interface|interface
name|TreeContext
block|{
comment|/**      * Reveals if the specified {@code PropertyState} is defined by the      * module that exposes this {@link TreeContext} instance.      *      * @param parent The parent tree of the property state.      * @param property The {@code PropertyState} to be tested.      * @return {@code true} if the specified property state is related to or      * defined by the security module.      */
name|boolean
name|definesProperty
parameter_list|(
annotation|@
name|Nonnull
name|Tree
name|parent
parameter_list|,
annotation|@
name|Nonnull
name|PropertyState
name|property
parameter_list|)
function_decl|;
comment|/**      * Reveals if the specified {@code Tree} is the root of a subtree defined by      * the module that exposes this {@link TreeContext} instance. Note,      * that in contrast to {@link #definesTree(Tree)}      * this method will only return {@code false} for any tree located in the      * subtree.      *      * @param tree The tree to be tested.      * @return {@code true} if the specified tree is the root of a subtree of items      * that are defined by the security module.      */
name|boolean
name|definesContextRoot
parameter_list|(
annotation|@
name|Nonnull
name|Tree
name|tree
parameter_list|)
function_decl|;
comment|/**      * Reveals if the specified {@code Tree} is defined by the      * module that exposes this {@link TreeContext} instance.      *      * @param tree The tree to be tested.      * @return {@code true} if the specified tree is related to or defined by the      * security module.      */
name|boolean
name|definesTree
parameter_list|(
annotation|@
name|Nonnull
name|Tree
name|tree
parameter_list|)
function_decl|;
comment|/**      * Reveals if the specified {@code TreeLocation} is defined by the      * module that exposes this {@link TreeContext} instance.      *      * @param location The tree location to be tested.      * @return {@code true} if the specified tree location is related to or      * defined by the security module.      */
name|boolean
name|definesLocation
parameter_list|(
annotation|@
name|Nonnull
name|TreeLocation
name|location
parameter_list|)
function_decl|;
comment|/**      * Reveals if the specified {@code Tree} defines repository internal information,      * which is not hidden by default.      *      * @param tree The tree to be tested.      * @return {@code true} if the specified tree defines repository internal information.      * @see org.apache.jackrabbit.oak.spi.state.NodeStateUtils#isHidden(String)      * @see org.apache.jackrabbit.oak.spi.state.NodeStateUtils#isHiddenPath(String)      */
name|boolean
name|definesInternal
parameter_list|(
annotation|@
name|Nonnull
name|Tree
name|tree
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

