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
package|;
end_package

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
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicBoolean
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
name|ImmutableList
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
name|junit
operator|.
name|Test
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
name|document
operator|.
name|Collection
operator|.
name|SETTINGS
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
name|document
operator|.
name|FormatVersion
operator|.
name|V0
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
name|document
operator|.
name|FormatVersion
operator|.
name|V1_0
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
name|document
operator|.
name|FormatVersion
operator|.
name|V1_2
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
name|document
operator|.
name|FormatVersion
operator|.
name|V1_4
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
name|document
operator|.
name|FormatVersion
operator|.
name|V1_6
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
name|document
operator|.
name|FormatVersion
operator|.
name|V1_8
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
name|document
operator|.
name|FormatVersion
operator|.
name|valueOf
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
name|assertEquals
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
name|assertSame
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
name|FormatVersionTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|canRead
parameter_list|()
block|{
name|assertTrue
argument_list|(
name|V1_8
operator|.
name|canRead
argument_list|(
name|V1_8
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|V1_8
operator|.
name|canRead
argument_list|(
name|V1_6
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|V1_8
operator|.
name|canRead
argument_list|(
name|V1_4
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|V1_8
operator|.
name|canRead
argument_list|(
name|V1_2
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|V1_8
operator|.
name|canRead
argument_list|(
name|V1_0
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|V1_8
operator|.
name|canRead
argument_list|(
name|V0
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|V1_6
operator|.
name|canRead
argument_list|(
name|V1_8
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|V1_6
operator|.
name|canRead
argument_list|(
name|V1_6
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|V1_6
operator|.
name|canRead
argument_list|(
name|V1_4
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|V1_6
operator|.
name|canRead
argument_list|(
name|V1_2
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|V1_6
operator|.
name|canRead
argument_list|(
name|V1_0
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|V1_6
operator|.
name|canRead
argument_list|(
name|V0
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|V1_4
operator|.
name|canRead
argument_list|(
name|V1_8
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|V1_4
operator|.
name|canRead
argument_list|(
name|V1_6
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|V1_4
operator|.
name|canRead
argument_list|(
name|V1_4
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|V1_4
operator|.
name|canRead
argument_list|(
name|V1_2
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|V1_4
operator|.
name|canRead
argument_list|(
name|V1_0
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|V1_4
operator|.
name|canRead
argument_list|(
name|V0
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|V1_2
operator|.
name|canRead
argument_list|(
name|V1_8
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|V1_2
operator|.
name|canRead
argument_list|(
name|V1_6
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|V1_2
operator|.
name|canRead
argument_list|(
name|V1_4
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|V1_2
operator|.
name|canRead
argument_list|(
name|V1_2
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|V1_2
operator|.
name|canRead
argument_list|(
name|V1_0
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|V1_2
operator|.
name|canRead
argument_list|(
name|V0
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|V1_0
operator|.
name|canRead
argument_list|(
name|V1_8
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|V1_0
operator|.
name|canRead
argument_list|(
name|V1_6
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|V1_0
operator|.
name|canRead
argument_list|(
name|V1_4
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|V1_0
operator|.
name|canRead
argument_list|(
name|V1_2
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|V1_0
operator|.
name|canRead
argument_list|(
name|V1_0
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|V1_0
operator|.
name|canRead
argument_list|(
name|V0
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|toStringValueOf
parameter_list|()
block|{
for|for
control|(
name|FormatVersion
name|v
range|:
name|FormatVersion
operator|.
name|values
argument_list|()
control|)
block|{
name|String
name|s
init|=
name|v
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertSame
argument_list|(
name|v
argument_list|,
name|valueOf
argument_list|(
name|s
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|valueOfUnknown
parameter_list|()
block|{
name|String
name|s
init|=
literal|"0.9.7"
decl_stmt|;
name|FormatVersion
name|v
init|=
name|valueOf
argument_list|(
name|s
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|s
argument_list|,
name|v
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|versionOf
parameter_list|()
throws|throws
name|Exception
block|{
name|DocumentStore
name|store
init|=
operator|new
name|MemoryDocumentStore
argument_list|()
decl_stmt|;
name|FormatVersion
name|v
init|=
name|FormatVersion
operator|.
name|versionOf
argument_list|(
name|store
argument_list|)
decl_stmt|;
name|assertSame
argument_list|(
name|V0
argument_list|,
name|v
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|writeTo
parameter_list|()
throws|throws
name|Exception
block|{
name|DocumentStore
name|store
init|=
operator|new
name|MemoryDocumentStore
argument_list|()
decl_stmt|;
comment|// must not write dummy version
name|assertFalse
argument_list|(
name|V0
operator|.
name|writeTo
argument_list|(
name|store
argument_list|)
argument_list|)
expr_stmt|;
comment|// upgrade
for|for
control|(
name|FormatVersion
name|v
range|:
name|ImmutableList
operator|.
name|of
argument_list|(
name|V1_0
argument_list|,
name|V1_2
argument_list|,
name|V1_4
argument_list|,
name|V1_6
argument_list|,
name|V1_8
argument_list|)
control|)
block|{
name|assertTrue
argument_list|(
name|v
operator|.
name|writeTo
argument_list|(
name|store
argument_list|)
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|v
argument_list|,
name|FormatVersion
operator|.
name|versionOf
argument_list|(
name|store
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|DocumentStoreException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|downgrade
parameter_list|()
throws|throws
name|Exception
block|{
name|DocumentStore
name|store
init|=
operator|new
name|MemoryDocumentStore
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|V1_4
operator|.
name|writeTo
argument_list|(
name|store
argument_list|)
argument_list|)
expr_stmt|;
comment|// must not downgrade
name|V1_2
operator|.
name|writeTo
argument_list|(
name|store
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|DocumentStoreException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|activeClusterNodes
parameter_list|()
throws|throws
name|Exception
block|{
name|DocumentStore
name|store
init|=
operator|new
name|MemoryDocumentStore
argument_list|()
decl_stmt|;
name|V1_0
operator|.
name|writeTo
argument_list|(
name|store
argument_list|)
expr_stmt|;
name|ClusterNodeInfo
name|info
init|=
name|ClusterNodeInfo
operator|.
name|getInstance
argument_list|(
name|store
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|info
operator|.
name|renewLease
argument_list|()
expr_stmt|;
name|V1_2
operator|.
name|writeTo
argument_list|(
name|store
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|DocumentStoreException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|concurrentUpdate1
parameter_list|()
throws|throws
name|Exception
block|{
name|DocumentStore
name|store
init|=
operator|new
name|MemoryDocumentStore
argument_list|()
block|{
specifier|private
specifier|final
name|AtomicBoolean
name|once
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
annotation|@
name|Override
specifier|public
parameter_list|<
name|T
extends|extends
name|Document
parameter_list|>
name|T
name|findAndUpdate
parameter_list|(
name|Collection
argument_list|<
name|T
argument_list|>
name|collection
parameter_list|,
name|UpdateOp
name|update
parameter_list|)
block|{
if|if
condition|(
name|collection
operator|==
name|SETTINGS
operator|&&
operator|!
name|once
operator|.
name|getAndSet
argument_list|(
literal|true
argument_list|)
condition|)
block|{
name|V1_2
operator|.
name|writeTo
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
return|return
name|super
operator|.
name|findAndUpdate
argument_list|(
name|collection
argument_list|,
name|update
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|V1_0
operator|.
name|writeTo
argument_list|(
name|store
argument_list|)
expr_stmt|;
name|V1_2
operator|.
name|writeTo
argument_list|(
name|store
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|DocumentStoreException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|concurrentUpdate2
parameter_list|()
throws|throws
name|Exception
block|{
name|DocumentStore
name|store
init|=
operator|new
name|MemoryDocumentStore
argument_list|()
block|{
specifier|private
specifier|final
name|AtomicBoolean
name|once
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
annotation|@
name|Override
specifier|public
parameter_list|<
name|T
extends|extends
name|Document
parameter_list|>
name|boolean
name|create
parameter_list|(
name|Collection
argument_list|<
name|T
argument_list|>
name|collection
parameter_list|,
name|List
argument_list|<
name|UpdateOp
argument_list|>
name|updateOps
parameter_list|)
block|{
if|if
condition|(
name|collection
operator|==
name|SETTINGS
operator|&&
operator|!
name|once
operator|.
name|getAndSet
argument_list|(
literal|true
argument_list|)
condition|)
block|{
name|V1_0
operator|.
name|writeTo
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
return|return
name|super
operator|.
name|create
argument_list|(
name|collection
argument_list|,
name|updateOps
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|V1_0
operator|.
name|writeTo
argument_list|(
name|store
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit
