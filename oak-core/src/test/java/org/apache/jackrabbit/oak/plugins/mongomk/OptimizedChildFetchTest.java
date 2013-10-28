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
name|mongomk
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
name|Sets
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
name|mongomk
operator|.
name|util
operator|.
name|Utils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
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
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
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
name|Set
import|;
end_import

begin_import
import|import static
name|junit
operator|.
name|framework
operator|.
name|Assert
operator|.
name|assertNotNull
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
name|assertThat
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
name|matchers
operator|.
name|JUnitMatchers
operator|.
name|hasItem
import|;
end_import

begin_class
specifier|public
class|class
name|OptimizedChildFetchTest
extends|extends
name|BaseMongoMKTest
block|{
specifier|private
name|TestDocumentStore
name|ds
init|=
operator|new
name|TestDocumentStore
argument_list|()
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|initMongoMK
parameter_list|()
block|{
name|mk
operator|=
operator|new
name|MongoMK
operator|.
name|Builder
argument_list|()
operator|.
name|setDocumentStore
argument_list|(
name|ds
argument_list|)
operator|.
name|open
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|checkForChildStatusFlag
parameter_list|()
block|{
name|String
name|head
init|=
name|mk
operator|.
name|getHeadRevision
argument_list|()
decl_stmt|;
name|mk
operator|.
name|commit
argument_list|(
literal|""
argument_list|,
literal|"+\"/root\":{}\n"
operator|+
literal|"+\"/root/a\":{}\n"
operator|+
literal|"+\"/root/a/b\":{}\n"
argument_list|,
name|head
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|hasChildren
argument_list|(
literal|"/root"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|hasChildren
argument_list|(
literal|"/root/a"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|hasChildren
argument_list|(
literal|"/root/a/b"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|checkForNoCallsToFetchChildForLeafNodes
parameter_list|()
block|{
name|String
name|head
init|=
name|mk
operator|.
name|getHeadRevision
argument_list|()
decl_stmt|;
name|String
name|rev
init|=
name|mk
operator|.
name|commit
argument_list|(
literal|""
argument_list|,
literal|"+\"/root\":{}\n"
operator|+
literal|"+\"/root/a\":{}\n"
operator|+
literal|"+\"/root/c\":{}\n"
operator|+
literal|"+\"/root/a/b\":{}\n"
argument_list|,
name|head
argument_list|,
literal|""
argument_list|)
decl_stmt|;
comment|//Clear the caches
name|ds
operator|.
name|paths
operator|.
name|clear
argument_list|()
expr_stmt|;
name|resetMK
argument_list|()
expr_stmt|;
comment|//Check that call is made to fetch child for non
comment|//leaf nodes
name|mk
operator|.
name|getNodes
argument_list|(
literal|"/root/a"
argument_list|,
name|rev
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|10
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|ds
operator|.
name|paths
argument_list|,
name|hasItem
argument_list|(
literal|"3:/root/a/"
argument_list|)
argument_list|)
expr_stmt|;
name|resetMK
argument_list|()
expr_stmt|;
name|ds
operator|.
name|paths
operator|.
name|clear
argument_list|()
expr_stmt|;
comment|//Check that no query is made to fetch children for
comment|//leaf nodes
name|assertNotNull
argument_list|(
name|mk
operator|.
name|getNodes
argument_list|(
literal|"/root/c"
argument_list|,
name|rev
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|10
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|mk
operator|.
name|getNodes
argument_list|(
literal|"/root/a/b"
argument_list|,
name|rev
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|10
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|ds
operator|.
name|paths
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|resetMK
parameter_list|()
block|{
name|disposeMongoMK
argument_list|()
expr_stmt|;
name|initMongoMK
argument_list|()
expr_stmt|;
block|}
specifier|private
name|boolean
name|hasChildren
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|NodeDocument
name|nd
init|=
name|mk
operator|.
name|getDocumentStore
argument_list|()
operator|.
name|find
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|Utils
operator|.
name|getIdFromPath
argument_list|(
name|path
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|nd
operator|.
name|hasChildren
argument_list|()
return|;
block|}
specifier|private
specifier|static
class|class
name|TestDocumentStore
extends|extends
name|MemoryDocumentStore
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|paths
init|=
name|Sets
operator|.
name|newHashSet
argument_list|()
decl_stmt|;
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
parameter_list|<
name|T
extends|extends
name|Document
parameter_list|>
name|List
argument_list|<
name|T
argument_list|>
name|query
parameter_list|(
name|Collection
argument_list|<
name|T
argument_list|>
name|collection
parameter_list|,
name|String
name|fromKey
parameter_list|,
name|String
name|toKey
parameter_list|,
name|String
name|indexedProperty
parameter_list|,
name|long
name|startValue
parameter_list|,
name|int
name|limit
parameter_list|)
block|{
name|paths
operator|.
name|add
argument_list|(
name|fromKey
argument_list|)
expr_stmt|;
return|return
name|super
operator|.
name|query
argument_list|(
name|collection
argument_list|,
name|fromKey
argument_list|,
name|toKey
argument_list|,
name|indexedProperty
argument_list|,
name|startValue
argument_list|,
name|limit
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

