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
name|console
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
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
name|commons
operator|.
name|PathUtils
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
name|NodeStore
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Maps
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
name|whiteboard
operator|.
name|Whiteboard
import|;
end_import

begin_comment
comment|/**  * Light weight session to a NodeStore, holding context information.  */
end_comment

begin_class
specifier|public
class|class
name|ConsoleSession
block|{
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|context
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|NodeStore
name|store
decl_stmt|;
specifier|private
specifier|final
name|Whiteboard
name|whiteboard
decl_stmt|;
specifier|private
name|ConsoleSession
parameter_list|(
name|NodeStore
name|store
parameter_list|,
name|Whiteboard
name|whiteboard
parameter_list|)
block|{
name|this
operator|.
name|store
operator|=
name|store
expr_stmt|;
name|this
operator|.
name|whiteboard
operator|=
name|whiteboard
expr_stmt|;
block|}
specifier|public
specifier|static
name|ConsoleSession
name|create
parameter_list|(
name|NodeStore
name|store
parameter_list|,
name|Whiteboard
name|whiteboard
parameter_list|)
block|{
return|return
operator|new
name|ConsoleSession
argument_list|(
name|store
argument_list|,
name|whiteboard
argument_list|)
return|;
block|}
comment|/**      * Returns the current working path. This method will return the path      * of the root node if none is set explicity.      *      * @return the current working path.      */
specifier|public
name|String
name|getWorkingPath
parameter_list|()
block|{
name|String
name|path
init|=
operator|(
name|String
operator|)
name|context
operator|.
name|get
argument_list|(
literal|"workingPath"
argument_list|)
decl_stmt|;
if|if
condition|(
name|path
operator|==
literal|null
condition|)
block|{
name|path
operator|=
literal|"/"
expr_stmt|;
name|context
operator|.
name|put
argument_list|(
literal|"workingPath"
argument_list|,
name|path
argument_list|)
expr_stmt|;
block|}
return|return
name|path
return|;
block|}
comment|/**      * Sets a new working path and returns the previously set.      *      * @param path the new working path.      * @return the previously set working path.      */
specifier|public
name|String
name|setWorkingPath
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|String
name|old
init|=
name|getWorkingPath
argument_list|()
decl_stmt|;
name|context
operator|.
name|put
argument_list|(
literal|"workingPath"
argument_list|,
name|path
argument_list|)
expr_stmt|;
return|return
name|old
return|;
block|}
comment|/**      * Creates and returns a checkpoint with the given lifetime in seconds.      *      * @param lifetimeSeconds the lifetime of the checkpoint in seconds.      * @return the checkpoint reference.      */
specifier|public
name|String
name|checkpoint
parameter_list|(
name|long
name|lifetimeSeconds
parameter_list|)
block|{
return|return
name|store
operator|.
name|checkpoint
argument_list|(
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|toMillis
argument_list|(
name|lifetimeSeconds
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * Retrieves the root node from a previously created checkpoint. The root      * node is available with {@link #getRoot()}.      *      * @param checkpoint the checkpoint reference.      */
specifier|public
name|void
name|retrieve
parameter_list|(
name|String
name|checkpoint
parameter_list|)
block|{
name|context
operator|.
name|put
argument_list|(
literal|"root"
argument_list|,
name|store
operator|.
name|retrieve
argument_list|(
name|checkpoint
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Returns the currently set root node. If {@link #isAutoRefresh()} is set,      * a fresh root node is retrieved from the store.      *      * @return the current root node.      */
specifier|public
name|NodeState
name|getRoot
parameter_list|()
block|{
name|NodeState
name|root
init|=
operator|(
name|NodeState
operator|)
name|context
operator|.
name|get
argument_list|(
literal|"root"
argument_list|)
decl_stmt|;
if|if
condition|(
name|root
operator|==
literal|null
operator|||
name|isAutoRefresh
argument_list|()
condition|)
block|{
name|root
operator|=
name|store
operator|.
name|getRoot
argument_list|()
expr_stmt|;
name|context
operator|.
name|put
argument_list|(
literal|"root"
argument_list|,
name|root
argument_list|)
expr_stmt|;
block|}
return|return
name|root
return|;
block|}
comment|/**      * @return the underlying node store.      */
specifier|public
name|NodeStore
name|getStore
parameter_list|()
block|{
return|return
name|store
return|;
block|}
specifier|public
name|Whiteboard
name|getWhiteboard
parameter_list|()
block|{
return|return
name|whiteboard
return|;
block|}
comment|/**      * The node state for the current working path. Possibly non-existent.      *      * @return the working node state.      */
annotation|@
name|NotNull
specifier|public
name|NodeState
name|getWorkingNode
parameter_list|()
block|{
name|NodeState
name|current
init|=
name|getRoot
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|element
range|:
name|PathUtils
operator|.
name|elements
argument_list|(
name|getWorkingPath
argument_list|()
argument_list|)
control|)
block|{
name|current
operator|=
name|current
operator|.
name|getChildNode
argument_list|(
name|element
argument_list|)
expr_stmt|;
block|}
return|return
name|current
return|;
block|}
comment|/**      * Enables or disables auto-refresh of the root node state on      * {@link #getRoot()}.      *      * @param enable enables or disables auto-refresh.      */
specifier|public
name|void
name|setAutoRefresh
parameter_list|(
name|boolean
name|enable
parameter_list|)
block|{
if|if
condition|(
name|enable
condition|)
block|{
name|context
operator|.
name|put
argument_list|(
literal|"auto-refresh"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|context
operator|.
name|remove
argument_list|(
literal|"auto-refresh"
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * @return<code>true</code> if auto-refresh is enabled;<code>false</code>      *          otherwise.      */
specifier|public
name|boolean
name|isAutoRefresh
parameter_list|()
block|{
return|return
name|context
operator|.
name|containsKey
argument_list|(
literal|"auto-refresh"
argument_list|)
return|;
block|}
comment|/**      * Performs a manual refresh of the root node state.      */
specifier|public
name|void
name|refresh
parameter_list|()
block|{
name|context
operator|.
name|put
argument_list|(
literal|"root"
argument_list|,
name|store
operator|.
name|getRoot
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

