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
name|query
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
name|Root
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
name|query
operator|.
name|ast
operator|.
name|NodeTypeInfoProvider
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
name|spi
operator|.
name|query
operator|.
name|QueryEngineSettings
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
name|spi
operator|.
name|query
operator|.
name|QueryIndexProvider
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
name|spi
operator|.
name|security
operator|.
name|authorization
operator|.
name|permission
operator|.
name|PermissionProvider
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
name|spi
operator|.
name|state
operator|.
name|NodeState
import|;
end_import

begin_comment
comment|/**  * An instance of this class provides the context for the execution of a query,  * which in essence captures a stable state of the content tree from the time  * the execution context was created.  */
end_comment

begin_class
specifier|public
class|class
name|ExecutionContext
block|{
comment|/**      * Base state used for index lookups.      */
specifier|private
specifier|final
name|NodeState
name|baseState
decl_stmt|;
specifier|private
specifier|final
name|Root
name|root
decl_stmt|;
specifier|private
specifier|final
name|QueryEngineSettings
name|settings
decl_stmt|;
specifier|private
specifier|final
name|QueryIndexProvider
name|indexProvider
decl_stmt|;
specifier|private
specifier|final
name|PermissionProvider
name|permissionProvider
decl_stmt|;
specifier|public
name|ExecutionContext
parameter_list|(
name|NodeState
name|baseState
parameter_list|,
name|Root
name|root
parameter_list|,
name|QueryEngineSettings
name|settings
parameter_list|,
name|QueryIndexProvider
name|indexProvider
parameter_list|,
name|PermissionProvider
name|permissionProvider
parameter_list|)
block|{
name|this
operator|.
name|baseState
operator|=
name|baseState
expr_stmt|;
name|this
operator|.
name|root
operator|=
name|root
expr_stmt|;
name|this
operator|.
name|settings
operator|=
name|settings
expr_stmt|;
name|this
operator|.
name|indexProvider
operator|=
name|indexProvider
expr_stmt|;
name|this
operator|.
name|permissionProvider
operator|=
name|permissionProvider
expr_stmt|;
block|}
comment|/**      * Used to evaluate the query (ie. read the existing node types, index      * definitions), doesn't need to be a secured version of a node state      *       * @return base state of the content tree against which the query runs.      */
annotation|@
name|Nonnull
specifier|public
name|NodeState
name|getBaseState
parameter_list|()
block|{
return|return
name|baseState
return|;
block|}
comment|/**      * Get the nodetype info provider.      *       * @return the provider      */
specifier|public
name|NodeTypeInfoProvider
name|getNodeTypeInfoProvider
parameter_list|()
block|{
return|return
operator|new
name|NodeStateNodeTypeInfoProvider
argument_list|(
name|baseState
argument_list|)
return|;
block|}
comment|/**      * Used to create the actual query results from the indexed paths, needs to      * be a secured version of a tree to take into account ACLs      *       * @return root of the content tree against which the query runs.      */
annotation|@
name|Nonnull
specifier|public
name|Root
name|getRoot
parameter_list|()
block|{
return|return
name|root
return|;
block|}
comment|/**      * @return Index provider for indexes matching the state of the content tree as      * returned from {@link #getBaseState()}.      */
annotation|@
name|Nonnull
specifier|public
name|QueryIndexProvider
name|getIndexProvider
parameter_list|()
block|{
return|return
name|indexProvider
return|;
block|}
specifier|public
name|QueryEngineSettings
name|getSettings
parameter_list|()
block|{
return|return
name|settings
return|;
block|}
annotation|@
name|CheckForNull
specifier|public
name|PermissionProvider
name|getPermissionProvider
parameter_list|()
block|{
return|return
name|permissionProvider
return|;
block|}
block|}
end_class

end_unit

