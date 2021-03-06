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
name|atomic
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Executors
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
name|ScheduledExecutorService
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
name|ThreadFactory
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
name|atomic
operator|.
name|AtomicReference
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Supplier
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
name|CommitFailedException
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
name|concurrent
operator|.
name|ExecutorCloser
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
name|osgi
operator|.
name|OsgiWhiteboard
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
name|commit
operator|.
name|CommitHook
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
name|commit
operator|.
name|CommitInfo
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
name|commit
operator|.
name|Editor
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
name|commit
operator|.
name|EditorProvider
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
name|Clusterable
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
name|NodeBuilder
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
name|framework
operator|.
name|BundleContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|osgi
operator|.
name|service
operator|.
name|component
operator|.
name|annotations
operator|.
name|Activate
import|;
end_import

begin_import
import|import
name|org
operator|.
name|osgi
operator|.
name|service
operator|.
name|component
operator|.
name|annotations
operator|.
name|Component
import|;
end_import

begin_import
import|import
name|org
operator|.
name|osgi
operator|.
name|service
operator|.
name|component
operator|.
name|annotations
operator|.
name|Deactivate
import|;
end_import

begin_import
import|import
name|org
operator|.
name|osgi
operator|.
name|service
operator|.
name|component
operator|.
name|annotations
operator|.
name|Reference
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
name|util
operator|.
name|concurrent
operator|.
name|ThreadFactoryBuilder
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|osgi
operator|.
name|service
operator|.
name|component
operator|.
name|annotations
operator|.
name|ReferenceCardinality
operator|.
name|OPTIONAL
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|osgi
operator|.
name|service
operator|.
name|component
operator|.
name|annotations
operator|.
name|ReferencePolicy
operator|.
name|DYNAMIC
import|;
end_import

begin_comment
comment|/**  * Provide an instance of {@link AtomicCounterEditor}. See {@link AtomicCounterEditor} for  * behavioural details.  */
end_comment

begin_class
annotation|@
name|Component
argument_list|(
name|property
operator|=
literal|"type=atomicCounter"
argument_list|,
name|service
operator|=
name|EditorProvider
operator|.
name|class
argument_list|)
specifier|public
class|class
name|AtomicCounterEditorProvider
implements|implements
name|EditorProvider
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|AtomicCounterEditorProvider
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|AtomicReference
argument_list|<
name|Clusterable
argument_list|>
name|cluster
init|=
operator|new
name|AtomicReference
argument_list|<
name|Clusterable
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
specifier|volatile
name|AtomicReference
argument_list|<
name|NodeStore
argument_list|>
name|store
init|=
operator|new
name|AtomicReference
argument_list|<
name|NodeStore
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
specifier|volatile
name|AtomicReference
argument_list|<
name|ScheduledExecutorService
argument_list|>
name|scheduler
init|=
operator|new
name|AtomicReference
argument_list|<
name|ScheduledExecutorService
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
specifier|volatile
name|AtomicReference
argument_list|<
name|Whiteboard
argument_list|>
name|whiteboard
init|=
operator|new
name|AtomicReference
argument_list|<
name|Whiteboard
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|Supplier
argument_list|<
name|Clusterable
argument_list|>
name|clusterSupplier
decl_stmt|;
specifier|private
specifier|final
name|Supplier
argument_list|<
name|ScheduledExecutorService
argument_list|>
name|schedulerSupplier
decl_stmt|;
specifier|private
specifier|final
name|Supplier
argument_list|<
name|NodeStore
argument_list|>
name|storeSupplier
decl_stmt|;
specifier|private
specifier|final
name|Supplier
argument_list|<
name|Whiteboard
argument_list|>
name|wbSupplier
decl_stmt|;
comment|/**      * OSGi oriented constructor where all the required dependencies will be taken care of.      */
specifier|public
name|AtomicCounterEditorProvider
parameter_list|()
block|{
name|clusterSupplier
operator|=
operator|new
name|Supplier
argument_list|<
name|Clusterable
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Clusterable
name|get
parameter_list|()
block|{
return|return
name|cluster
operator|.
name|get
argument_list|()
return|;
block|}
block|}
expr_stmt|;
name|schedulerSupplier
operator|=
operator|new
name|Supplier
argument_list|<
name|ScheduledExecutorService
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|ScheduledExecutorService
name|get
parameter_list|()
block|{
return|return
name|scheduler
operator|.
name|get
argument_list|()
return|;
block|}
block|}
expr_stmt|;
name|storeSupplier
operator|=
operator|new
name|Supplier
argument_list|<
name|NodeStore
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|NodeStore
name|get
parameter_list|()
block|{
return|return
name|store
operator|.
name|get
argument_list|()
return|;
block|}
block|}
expr_stmt|;
name|wbSupplier
operator|=
operator|new
name|Supplier
argument_list|<
name|Whiteboard
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Whiteboard
name|get
parameter_list|()
block|{
return|return
name|whiteboard
operator|.
name|get
argument_list|()
return|;
block|}
block|}
expr_stmt|;
block|}
comment|/**      *<p>      * Plain Java oriented constructor. Refer to      * {@link AtomicCounterEditor#AtomicCounterEditor(NodeBuilder, String, ScheduledExecutorService, NodeStore, Whiteboard)}      * for constructions details of the actual editor.      *</p>      *       *<p>      * Based on the use case this may need an already set of the constructor parameters during the      * repository construction. Please ensure they're registered before this provider is registered.      *</p>      *       * @param clusterInfo cluster node information      * @param executor the executor for running asynchronously.      * @param store reference to the NodeStore.      * @param whiteboard the underlying board for picking up the registered {@link CommitHook}      */
specifier|public
name|AtomicCounterEditorProvider
parameter_list|(
annotation|@
name|Nullable
name|Supplier
argument_list|<
name|Clusterable
argument_list|>
name|clusterInfo
parameter_list|,
annotation|@
name|Nullable
name|Supplier
argument_list|<
name|ScheduledExecutorService
argument_list|>
name|executor
parameter_list|,
annotation|@
name|Nullable
name|Supplier
argument_list|<
name|NodeStore
argument_list|>
name|store
parameter_list|,
annotation|@
name|Nullable
name|Supplier
argument_list|<
name|Whiteboard
argument_list|>
name|whiteboard
parameter_list|)
block|{
name|this
operator|.
name|clusterSupplier
operator|=
name|clusterInfo
expr_stmt|;
name|this
operator|.
name|schedulerSupplier
operator|=
name|executor
expr_stmt|;
name|this
operator|.
name|storeSupplier
operator|=
name|store
expr_stmt|;
name|this
operator|.
name|wbSupplier
operator|=
name|whiteboard
expr_stmt|;
block|}
comment|/**      * convenience method wrapping logic around {@link AtomicReference}      *       * @return      */
specifier|private
name|String
name|getInstanceId
parameter_list|()
block|{
name|Clusterable
name|c
init|=
name|clusterSupplier
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|c
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
return|return
name|c
operator|.
name|getInstanceId
argument_list|()
return|;
block|}
block|}
comment|/**      * convenience method wrapping logic around {@link AtomicReference}      *       * @return      */
specifier|private
name|ScheduledExecutorService
name|getScheduler
parameter_list|()
block|{
return|return
name|schedulerSupplier
operator|.
name|get
argument_list|()
return|;
block|}
comment|/**      * convenience method wrapping logic around {@link AtomicReference}      *       * @return      */
specifier|private
name|NodeStore
name|getStore
parameter_list|()
block|{
return|return
name|storeSupplier
operator|.
name|get
argument_list|()
return|;
block|}
comment|/**      * Convenience method wrapping logic around {@link AtomicReference}      *       * @return      */
specifier|private
name|Whiteboard
name|getBoard
parameter_list|()
block|{
return|return
name|wbSupplier
operator|.
name|get
argument_list|()
return|;
block|}
annotation|@
name|Activate
specifier|public
name|void
name|activate
parameter_list|(
name|BundleContext
name|context
parameter_list|)
block|{
name|whiteboard
operator|.
name|set
argument_list|(
operator|new
name|OsgiWhiteboard
argument_list|(
name|context
argument_list|)
argument_list|)
expr_stmt|;
name|ThreadFactory
name|tf
init|=
operator|new
name|ThreadFactoryBuilder
argument_list|()
operator|.
name|setNameFormat
argument_list|(
literal|"atomic-counter-%d"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|scheduler
operator|.
name|set
argument_list|(
name|Executors
operator|.
name|newScheduledThreadPool
argument_list|(
literal|10
argument_list|,
name|tf
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Deactivate
specifier|public
name|void
name|deactivate
parameter_list|()
block|{
name|ScheduledExecutorService
name|ses
init|=
name|getScheduler
argument_list|()
decl_stmt|;
if|if
condition|(
name|ses
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"No ScheduledExecutorService found"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Shutting down ScheduledExecutorService"
argument_list|)
expr_stmt|;
operator|new
name|ExecutorCloser
argument_list|(
name|ses
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Reference
argument_list|(
name|name
operator|=
literal|"cluster"
argument_list|,
name|policy
operator|=
name|DYNAMIC
argument_list|,
name|cardinality
operator|=
name|OPTIONAL
argument_list|)
specifier|protected
name|void
name|bindCluster
parameter_list|(
name|Clusterable
name|store
parameter_list|)
block|{
name|this
operator|.
name|cluster
operator|.
name|set
argument_list|(
name|store
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|unbindCluster
parameter_list|(
name|Clusterable
name|store
parameter_list|)
block|{
name|this
operator|.
name|cluster
operator|.
name|compareAndSet
argument_list|(
name|store
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Reference
argument_list|(
name|name
operator|=
literal|"store"
argument_list|,
name|policy
operator|=
name|DYNAMIC
argument_list|,
name|cardinality
operator|=
name|OPTIONAL
argument_list|)
specifier|protected
name|void
name|bindStore
parameter_list|(
name|NodeStore
name|store
parameter_list|)
block|{
name|this
operator|.
name|store
operator|.
name|set
argument_list|(
name|store
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|unbindStore
parameter_list|(
name|NodeStore
name|store
parameter_list|)
block|{
name|this
operator|.
name|store
operator|.
name|compareAndSet
argument_list|(
name|store
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Editor
name|getRootEditor
parameter_list|(
specifier|final
name|NodeState
name|before
parameter_list|,
specifier|final
name|NodeState
name|after
parameter_list|,
specifier|final
name|NodeBuilder
name|builder
parameter_list|,
specifier|final
name|CommitInfo
name|info
parameter_list|)
throws|throws
name|CommitFailedException
block|{
return|return
operator|new
name|AtomicCounterEditor
argument_list|(
name|builder
argument_list|,
name|getInstanceId
argument_list|()
argument_list|,
name|getScheduler
argument_list|()
argument_list|,
name|getStore
argument_list|()
argument_list|,
name|getBoard
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

