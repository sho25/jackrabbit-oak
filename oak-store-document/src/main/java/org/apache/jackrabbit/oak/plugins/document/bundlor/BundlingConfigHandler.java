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
name|plugins
operator|.
name|document
operator|.
name|bundlor
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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
name|Executor
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
name|javax
operator|.
name|annotation
operator|.
name|Nullable
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
name|Iterables
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
name|commit
operator|.
name|BackgroundObserver
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
name|BackgroundObserverMBean
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
name|DefaultEditor
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
name|EditorDiff
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
name|Observable
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
name|Observer
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
name|SubtreeEditor
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
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
operator|.
name|checkNotNull
import|;
end_import

begin_import
import|import static
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
name|memory
operator|.
name|EmptyNodeState
operator|.
name|EMPTY_NODE
import|;
end_import

begin_class
specifier|public
class|class
name|BundlingConfigHandler
implements|implements
name|Observer
implements|,
name|Closeable
block|{
specifier|public
specifier|static
specifier|final
name|String
name|DOCUMENT_NODE_STORE
init|=
literal|"rep:documentStore"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|BUNDLOR
init|=
literal|"bundlor"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|CONFIG_PATH
init|=
literal|"/jcr:system/rep:documentStore/bundlor"
decl_stmt|;
specifier|private
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|getClass
argument_list|()
argument_list|)
decl_stmt|;
specifier|private
name|NodeState
name|root
init|=
name|EMPTY_NODE
decl_stmt|;
specifier|private
name|BackgroundObserver
name|backgroundObserver
decl_stmt|;
specifier|private
name|Closeable
name|observerRegistration
decl_stmt|;
specifier|private
name|boolean
name|enabled
decl_stmt|;
specifier|private
specifier|volatile
name|BundledTypesRegistry
name|registry
init|=
name|BundledTypesRegistry
operator|.
name|NOOP
decl_stmt|;
specifier|private
name|Editor
name|changeDetector
init|=
operator|new
name|SubtreeEditor
argument_list|(
operator|new
name|DefaultEditor
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|leave
parameter_list|(
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|recreateRegistry
argument_list|(
name|after
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|,
name|Iterables
operator|.
name|toArray
argument_list|(
name|PathUtils
operator|.
name|elements
argument_list|(
name|CONFIG_PATH
argument_list|)
argument_list|,
name|String
operator|.
name|class
argument_list|)
argument_list|)
decl_stmt|;
annotation|@
name|Override
specifier|public
specifier|synchronized
name|void
name|contentChanged
parameter_list|(
annotation|@
name|Nonnull
name|NodeState
name|root
parameter_list|,
annotation|@
name|Nonnull
name|CommitInfo
name|info
parameter_list|)
block|{
name|EditorDiff
operator|.
name|process
argument_list|(
name|changeDetector
argument_list|,
name|this
operator|.
name|root
argument_list|,
name|root
argument_list|)
expr_stmt|;
name|this
operator|.
name|root
operator|=
name|root
expr_stmt|;
block|}
specifier|public
name|BundlingHandler
name|newBundlingHandler
parameter_list|()
block|{
return|return
operator|new
name|BundlingHandler
argument_list|(
name|registry
argument_list|)
return|;
block|}
specifier|public
name|void
name|initialize
parameter_list|(
name|Observable
name|nodeStore
parameter_list|,
name|Executor
name|executor
parameter_list|)
block|{
name|registerObserver
argument_list|(
name|nodeStore
argument_list|,
name|executor
argument_list|)
expr_stmt|;
comment|//If bundling is disabled then initialize would not be invoked
comment|//NOOP registry would get used effectively disabling bundling for
comment|//new nodes
name|enabled
operator|=
literal|true
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Bundling of nodes enabled"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|unregisterObserver
argument_list|()
expr_stmt|;
block|}
specifier|public
name|BackgroundObserverMBean
name|getMBean
parameter_list|()
block|{
return|return
name|checkNotNull
argument_list|(
name|backgroundObserver
argument_list|)
operator|.
name|getMBean
argument_list|()
return|;
block|}
specifier|public
name|boolean
name|isEnabled
parameter_list|()
block|{
return|return
name|enabled
return|;
block|}
name|BundledTypesRegistry
name|getRegistry
parameter_list|()
block|{
return|return
name|registry
return|;
block|}
specifier|private
name|void
name|recreateRegistry
parameter_list|(
name|NodeState
name|nodeState
parameter_list|)
block|{
comment|//TODO Any sanity checks
name|registry
operator|=
name|BundledTypesRegistry
operator|.
name|from
argument_list|(
name|nodeState
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Refreshing the BundledTypesRegistry"
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|registerObserver
parameter_list|(
name|Observable
name|observable
parameter_list|,
name|Executor
name|executor
parameter_list|)
block|{
name|backgroundObserver
operator|=
operator|new
name|BackgroundObserver
argument_list|(
name|this
argument_list|,
name|executor
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|observerRegistration
operator|=
name|observable
operator|.
name|addObserver
argument_list|(
name|backgroundObserver
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|unregisterObserver
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|backgroundObserver
operator|!=
literal|null
condition|)
block|{
name|observerRegistration
operator|.
name|close
argument_list|()
expr_stmt|;
name|backgroundObserver
operator|.
name|close
argument_list|()
expr_stmt|;
name|observerRegistration
operator|=
literal|null
expr_stmt|;
name|backgroundObserver
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit
