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
name|document
operator|.
name|util
package|;
end_package

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
name|Lists
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
name|plugins
operator|.
name|document
operator|.
name|Collection
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
name|plugins
operator|.
name|document
operator|.
name|Document
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
name|plugins
operator|.
name|document
operator|.
name|DocumentMKBuilderProvider
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
name|plugins
operator|.
name|document
operator|.
name|DocumentNodeStore
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
name|plugins
operator|.
name|document
operator|.
name|DocumentStore
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
name|plugins
operator|.
name|document
operator|.
name|UpdateOp
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
name|plugins
operator|.
name|document
operator|.
name|memory
operator|.
name|MemoryDocumentStore
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
name|EmptyHook
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
name|junit
operator|.
name|Assert
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Rule
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|InvocationHandler
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|InvocationTargetException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Method
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Proxy
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertFalse
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_class
specifier|public
class|class
name|ReadOnlyDocumentStoreWrapperTest
block|{
annotation|@
name|Rule
specifier|public
name|DocumentMKBuilderProvider
name|builderProvider
init|=
operator|new
name|DocumentMKBuilderProvider
argument_list|()
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|testPassthrough
parameter_list|()
throws|throws
name|NoSuchMethodException
throws|,
name|InvocationTargetException
throws|,
name|IllegalAccessException
block|{
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|disallowedMethods
init|=
name|Lists
operator|.
name|newArrayList
argument_list|(
literal|"create"
argument_list|,
literal|"update"
argument_list|,
literal|"remove"
argument_list|,
literal|"createOrUpdate"
argument_list|,
literal|"findAndUpdate"
argument_list|)
decl_stmt|;
name|InvocationHandler
name|handler
init|=
operator|new
name|InvocationHandler
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Object
name|invoke
parameter_list|(
name|Object
name|proxy
parameter_list|,
name|Method
name|method
parameter_list|,
name|Object
index|[]
name|args
parameter_list|)
throws|throws
name|Throwable
block|{
name|String
name|methodName
init|=
name|method
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
name|disallowedMethods
operator|.
name|contains
argument_list|(
name|methodName
argument_list|)
condition|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Invalid passthrough of method (%s) with params %s"
argument_list|,
name|method
argument_list|,
name|Arrays
operator|.
name|toString
argument_list|(
name|args
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
literal|"determineServerTimeDifferenceMillis"
operator|.
name|equals
argument_list|(
name|methodName
argument_list|)
condition|)
block|{
return|return
operator|new
name|Long
argument_list|(
literal|0
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
block|}
decl_stmt|;
name|DocumentStore
name|proxyStore
init|=
operator|(
name|DocumentStore
operator|)
name|Proxy
operator|.
name|newProxyInstance
argument_list|(
name|DocumentStore
operator|.
name|class
operator|.
name|getClassLoader
argument_list|()
argument_list|,
operator|new
name|Class
index|[]
block|{
name|DocumentStore
operator|.
name|class
block|}
argument_list|,
name|handler
argument_list|)
decl_stmt|;
name|DocumentStore
name|readOnlyStore
init|=
name|ReadOnlyDocumentStoreWrapperFactory
operator|.
name|getInstance
argument_list|(
name|proxyStore
argument_list|)
decl_stmt|;
name|Collection
argument_list|<
name|?
extends|extends
name|Document
argument_list|>
index|[]
name|collections
init|=
operator|new
name|Collection
index|[]
block|{
name|Collection
operator|.
name|CLUSTER_NODES
block|,
name|Collection
operator|.
name|JOURNAL
block|,
name|Collection
operator|.
name|NODES
block|,
name|Collection
operator|.
name|SETTINGS
block|}
decl_stmt|;
for|for
control|(
name|Collection
name|collection
range|:
name|collections
control|)
block|{
name|readOnlyStore
operator|.
name|find
argument_list|(
name|collection
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|readOnlyStore
operator|.
name|find
argument_list|(
name|collection
argument_list|,
literal|null
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|readOnlyStore
operator|.
name|query
argument_list|(
name|collection
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|readOnlyStore
operator|.
name|query
argument_list|(
name|collection
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|boolean
name|uoeThrown
init|=
literal|false
decl_stmt|;
try|try
block|{
name|readOnlyStore
operator|.
name|remove
argument_list|(
name|collection
argument_list|,
literal|""
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedOperationException
name|uoe
parameter_list|)
block|{
comment|//catch uoe thrown by read only wrapper
name|uoeThrown
operator|=
literal|true
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"remove must throw UnsupportedOperationException"
argument_list|,
name|uoeThrown
argument_list|)
expr_stmt|;
name|uoeThrown
operator|=
literal|false
expr_stmt|;
try|try
block|{
name|readOnlyStore
operator|.
name|remove
argument_list|(
name|collection
argument_list|,
name|Lists
operator|.
expr|<
name|String
operator|>
name|newArrayList
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedOperationException
name|uoe
parameter_list|)
block|{
comment|//catch uoe thrown by read only wrapper
name|uoeThrown
operator|=
literal|true
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"remove must throw UnsupportedOperationException"
argument_list|,
name|uoeThrown
argument_list|)
expr_stmt|;
name|uoeThrown
operator|=
literal|false
expr_stmt|;
try|try
block|{
name|readOnlyStore
operator|.
name|remove
argument_list|(
name|collection
argument_list|,
name|Maps
operator|.
expr|<
name|String
argument_list|,
name|Map
argument_list|<
name|UpdateOp
operator|.
name|Key
argument_list|,
name|UpdateOp
operator|.
name|Condition
argument_list|>
operator|>
name|newHashMap
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedOperationException
name|uoe
parameter_list|)
block|{
comment|//catch uoe thrown by read only wrapper
name|uoeThrown
operator|=
literal|true
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"remove must throw UnsupportedOperationException"
argument_list|,
name|uoeThrown
argument_list|)
expr_stmt|;
name|uoeThrown
operator|=
literal|false
expr_stmt|;
try|try
block|{
name|readOnlyStore
operator|.
name|create
argument_list|(
name|collection
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedOperationException
name|uoe
parameter_list|)
block|{
comment|//catch uoe thrown by read only wrapper
name|uoeThrown
operator|=
literal|true
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"create must throw UnsupportedOperationException"
argument_list|,
name|uoeThrown
argument_list|)
expr_stmt|;
name|uoeThrown
operator|=
literal|false
expr_stmt|;
try|try
block|{
name|readOnlyStore
operator|.
name|update
argument_list|(
name|collection
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedOperationException
name|uoe
parameter_list|)
block|{
comment|//catch uoe thrown by read only wrapper
name|uoeThrown
operator|=
literal|true
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"update must throw UnsupportedOperationException"
argument_list|,
name|uoeThrown
argument_list|)
expr_stmt|;
name|uoeThrown
operator|=
literal|false
expr_stmt|;
try|try
block|{
name|readOnlyStore
operator|.
name|createOrUpdate
argument_list|(
name|collection
argument_list|,
operator|(
name|UpdateOp
operator|)
literal|null
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedOperationException
name|uoe
parameter_list|)
block|{
comment|//catch uoe thrown by read only wrapper
name|uoeThrown
operator|=
literal|true
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"createOrUpdate must throw UnsupportedOperationException"
argument_list|,
name|uoeThrown
argument_list|)
expr_stmt|;
name|uoeThrown
operator|=
literal|false
expr_stmt|;
try|try
block|{
name|readOnlyStore
operator|.
name|createOrUpdate
argument_list|(
name|collection
argument_list|,
name|Lists
operator|.
expr|<
name|UpdateOp
operator|>
name|newArrayList
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedOperationException
name|uoe
parameter_list|)
block|{
comment|//catch uoe thrown by read only wrapper
name|uoeThrown
operator|=
literal|true
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"createOrUpdate must throw UnsupportedOperationException"
argument_list|,
name|uoeThrown
argument_list|)
expr_stmt|;
name|uoeThrown
operator|=
literal|false
expr_stmt|;
try|try
block|{
name|readOnlyStore
operator|.
name|findAndUpdate
argument_list|(
name|collection
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedOperationException
name|uoe
parameter_list|)
block|{
comment|//catch uoe thrown by read only wrapper
name|uoeThrown
operator|=
literal|true
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"findAndUpdate must throw UnsupportedOperationException"
argument_list|,
name|uoeThrown
argument_list|)
expr_stmt|;
name|readOnlyStore
operator|.
name|invalidateCache
argument_list|(
name|collection
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|readOnlyStore
operator|.
name|getIfCached
argument_list|(
name|collection
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
name|readOnlyStore
operator|.
name|invalidateCache
argument_list|()
expr_stmt|;
name|readOnlyStore
operator|.
name|invalidateCache
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|readOnlyStore
operator|.
name|dispose
argument_list|()
expr_stmt|;
name|readOnlyStore
operator|.
name|setReadWriteMode
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|readOnlyStore
operator|.
name|getCacheStats
argument_list|()
expr_stmt|;
name|readOnlyStore
operator|.
name|getMetadata
argument_list|()
expr_stmt|;
name|readOnlyStore
operator|.
name|determineServerTimeDifferenceMillis
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|backgroundRead
parameter_list|()
throws|throws
name|Exception
block|{
name|DocumentStore
name|docStore
init|=
operator|new
name|MemoryDocumentStore
argument_list|()
decl_stmt|;
name|DocumentNodeStore
name|store
init|=
name|builderProvider
operator|.
name|newBuilder
argument_list|()
operator|.
name|setAsyncDelay
argument_list|(
literal|0
argument_list|)
operator|.
name|setDocumentStore
argument_list|(
name|docStore
argument_list|)
operator|.
name|setClusterId
argument_list|(
literal|2
argument_list|)
operator|.
name|getNodeStore
argument_list|()
decl_stmt|;
name|DocumentNodeStore
name|readOnlyStore
init|=
name|builderProvider
operator|.
name|newBuilder
argument_list|()
operator|.
name|setAsyncDelay
argument_list|(
literal|0
argument_list|)
operator|.
name|setDocumentStore
argument_list|(
name|docStore
argument_list|)
operator|.
name|setClusterId
argument_list|(
literal|1
argument_list|)
operator|.
name|setReadOnlyMode
argument_list|()
operator|.
name|getNodeStore
argument_list|()
decl_stmt|;
name|NodeBuilder
name|builder
init|=
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"node"
argument_list|)
expr_stmt|;
name|store
operator|.
name|merge
argument_list|(
name|builder
argument_list|,
name|EmptyHook
operator|.
name|INSTANCE
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
name|store
operator|.
name|runBackgroundOperations
argument_list|()
expr_stmt|;
comment|// at this point node must not be visible
name|assertFalse
argument_list|(
name|readOnlyStore
operator|.
name|getRoot
argument_list|()
operator|.
name|hasChildNode
argument_list|(
literal|"node"
argument_list|)
argument_list|)
expr_stmt|;
name|readOnlyStore
operator|.
name|runBackgroundOperations
argument_list|()
expr_stmt|;
comment|// at this point node should get visible
name|assertTrue
argument_list|(
name|readOnlyStore
operator|.
name|getRoot
argument_list|()
operator|.
name|hasChildNode
argument_list|(
literal|"node"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit
