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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
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
name|Random
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
name|plugins
operator|.
name|document
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
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|spi
operator|.
name|blob
operator|.
name|BlobStore
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
name|After
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
import|import static
name|java
operator|.
name|util
operator|.
name|Arrays
operator|.
name|asList
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
name|fail
import|;
end_import

begin_comment
comment|/**  * Base class for journal related tests.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractJournalTest
block|{
specifier|protected
name|TestBuilder
name|builder
decl_stmt|;
specifier|protected
name|List
argument_list|<
name|DocumentMK
argument_list|>
name|mks
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
specifier|protected
name|Random
name|random
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setup
parameter_list|()
block|{
name|random
operator|=
operator|new
name|Random
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Before
annotation|@
name|After
specifier|public
name|void
name|clear
parameter_list|()
block|{
for|for
control|(
name|DocumentMK
name|mk
range|:
name|mks
control|)
block|{
name|mk
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
name|mks
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
specifier|protected
specifier|static
name|void
name|renewClusterIdLease
parameter_list|(
name|DocumentNodeStore
name|store
parameter_list|)
block|{
name|store
operator|.
name|renewClusterIdLease
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|Set
argument_list|<
name|String
argument_list|>
name|choose
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|paths
parameter_list|,
name|int
name|howMany
parameter_list|)
block|{
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|result
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
while|while
condition|(
name|result
operator|.
name|size
argument_list|()
operator|<
name|howMany
condition|)
block|{
name|result
operator|.
name|add
argument_list|(
name|paths
operator|.
name|get
argument_list|(
name|random
operator|.
name|nextInt
argument_list|(
name|paths
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
specifier|protected
name|List
argument_list|<
name|String
argument_list|>
name|createRandomPaths
parameter_list|(
name|int
name|depth
parameter_list|,
name|int
name|avgChildrenPerLevel
parameter_list|,
name|int
name|num
parameter_list|)
block|{
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|result
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
while|while
condition|(
name|result
operator|.
name|size
argument_list|()
operator|<
name|num
condition|)
block|{
name|result
operator|.
name|add
argument_list|(
name|createRandomPath
argument_list|(
name|depth
argument_list|,
name|avgChildrenPerLevel
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|(
name|result
argument_list|)
return|;
block|}
specifier|protected
name|String
name|createRandomPath
parameter_list|(
name|int
name|depth
parameter_list|,
name|int
name|avgChildrenPerLevel
parameter_list|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|depth
condition|;
name|i
operator|++
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"r"
argument_list|)
operator|.
name|append
argument_list|(
name|random
operator|.
name|nextInt
argument_list|(
name|avgChildrenPerLevel
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|protected
name|void
name|assertDocCache
parameter_list|(
name|DocumentNodeStore
name|ns
parameter_list|,
name|boolean
name|expected
parameter_list|,
name|String
name|path
parameter_list|)
block|{
name|String
name|id
init|=
name|Utils
operator|.
name|getIdFromPath
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|boolean
name|exists
init|=
name|ns
operator|.
name|getDocumentStore
argument_list|()
operator|.
name|getIfCached
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|id
argument_list|)
operator|!=
literal|null
decl_stmt|;
if|if
condition|(
name|exists
operator|!=
name|expected
condition|)
block|{
if|if
condition|(
name|expected
condition|)
block|{
name|fail
argument_list|(
literal|"assertDocCache: did not find in cache even though expected: "
operator|+
name|path
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|fail
argument_list|(
literal|"assertDocCache: found in cache even though not expected: "
operator|+
name|path
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|protected
name|void
name|setProperty
parameter_list|(
name|DocumentNodeStore
name|ns
parameter_list|,
name|String
name|path
parameter_list|,
name|String
name|key
parameter_list|,
name|String
name|value
parameter_list|,
name|boolean
name|runBgOpsAfterCreation
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|NodeBuilder
name|rootBuilder
init|=
name|ns
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|doGetOrCreate
argument_list|(
name|rootBuilder
argument_list|,
name|path
argument_list|)
operator|.
name|setProperty
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|ns
operator|.
name|merge
argument_list|(
name|rootBuilder
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
if|if
condition|(
name|runBgOpsAfterCreation
condition|)
block|{
name|ns
operator|.
name|runBackgroundOperations
argument_list|()
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|getOrCreate
parameter_list|(
name|DocumentNodeStore
name|ns
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|paths
parameter_list|,
name|boolean
name|runBgOpsAfterCreation
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|NodeBuilder
name|rootBuilder
init|=
name|ns
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|path
range|:
name|paths
control|)
block|{
name|doGetOrCreate
argument_list|(
name|rootBuilder
argument_list|,
name|path
argument_list|)
expr_stmt|;
block|}
name|ns
operator|.
name|merge
argument_list|(
name|rootBuilder
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
if|if
condition|(
name|runBgOpsAfterCreation
condition|)
block|{
name|ns
operator|.
name|runBackgroundOperations
argument_list|()
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|getOrCreate
parameter_list|(
name|DocumentNodeStore
name|ns
parameter_list|,
name|String
name|path
parameter_list|,
name|boolean
name|runBgOpsAfterCreation
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|NodeBuilder
name|rootBuilder
init|=
name|ns
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|doGetOrCreate
argument_list|(
name|rootBuilder
argument_list|,
name|path
argument_list|)
expr_stmt|;
name|ns
operator|.
name|merge
argument_list|(
name|rootBuilder
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
if|if
condition|(
name|runBgOpsAfterCreation
condition|)
block|{
name|ns
operator|.
name|runBackgroundOperations
argument_list|()
expr_stmt|;
block|}
block|}
specifier|protected
name|NodeBuilder
name|doGetOrCreate
parameter_list|(
name|NodeBuilder
name|builder
parameter_list|,
name|String
name|path
parameter_list|)
block|{
name|String
index|[]
name|parts
init|=
name|path
operator|.
name|split
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|parts
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|builder
operator|=
name|builder
operator|.
name|child
argument_list|(
name|parts
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
return|;
block|}
specifier|protected
name|void
name|assertJournalEntries
parameter_list|(
name|DocumentNodeStore
name|ds
parameter_list|,
name|String
modifier|...
name|expectedChanges
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|exp
init|=
operator|new
name|LinkedList
argument_list|<
name|String
argument_list|>
argument_list|(
name|asList
argument_list|(
name|expectedChanges
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|boolean
name|branch
range|:
operator|new
name|Boolean
index|[]
block|{
literal|false
block|,
literal|true
block|}
control|)
block|{
name|String
name|fromKey
init|=
name|JournalEntry
operator|.
name|asId
argument_list|(
operator|new
name|Revision
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
name|ds
operator|.
name|getClusterId
argument_list|()
argument_list|,
name|branch
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|toKey
init|=
name|JournalEntry
operator|.
name|asId
argument_list|(
operator|new
name|Revision
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
literal|1000
argument_list|,
literal|0
argument_list|,
name|ds
operator|.
name|getClusterId
argument_list|()
argument_list|,
name|branch
argument_list|)
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|JournalEntry
argument_list|>
name|entries
init|=
name|ds
operator|.
name|getDocumentStore
argument_list|()
operator|.
name|query
argument_list|(
name|Collection
operator|.
name|JOURNAL
argument_list|,
name|fromKey
argument_list|,
name|toKey
argument_list|,
name|expectedChanges
operator|.
name|length
operator|+
literal|5
argument_list|)
decl_stmt|;
if|if
condition|(
name|entries
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
for|for
control|(
name|JournalEntry
name|journalEntry
range|:
name|entries
control|)
block|{
if|if
condition|(
operator|!
name|exp
operator|.
name|remove
argument_list|(
name|journalEntry
operator|.
name|get
argument_list|(
literal|"_c"
argument_list|)
argument_list|)
condition|)
block|{
name|fail
argument_list|(
literal|"Found an unexpected change: "
operator|+
name|journalEntry
operator|.
name|get
argument_list|(
literal|"_c"
argument_list|)
operator|+
literal|", while all I expected was: "
operator|+
name|asList
argument_list|(
name|expectedChanges
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
if|if
condition|(
name|exp
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|fail
argument_list|(
literal|"Did not find all expected changes, left over: "
operator|+
name|exp
operator|+
literal|" (from original list which is: "
operator|+
name|asList
argument_list|(
name|expectedChanges
argument_list|)
operator|+
literal|")"
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|int
name|countJournalEntries
parameter_list|(
name|DocumentNodeStore
name|ds
parameter_list|,
name|int
name|max
parameter_list|)
block|{
name|int
name|total
init|=
literal|0
decl_stmt|;
for|for
control|(
name|boolean
name|branch
range|:
operator|new
name|Boolean
index|[]
block|{
literal|false
block|,
literal|true
block|}
control|)
block|{
name|String
name|fromKey
init|=
name|JournalEntry
operator|.
name|asId
argument_list|(
operator|new
name|Revision
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
name|ds
operator|.
name|getClusterId
argument_list|()
argument_list|,
name|branch
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|toKey
init|=
name|JournalEntry
operator|.
name|asId
argument_list|(
operator|new
name|Revision
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
literal|1000
argument_list|,
literal|0
argument_list|,
name|ds
operator|.
name|getClusterId
argument_list|()
argument_list|,
name|branch
argument_list|)
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|JournalEntry
argument_list|>
name|entries
init|=
name|ds
operator|.
name|getDocumentStore
argument_list|()
operator|.
name|query
argument_list|(
name|Collection
operator|.
name|JOURNAL
argument_list|,
name|fromKey
argument_list|,
name|toKey
argument_list|,
name|max
argument_list|)
decl_stmt|;
name|total
operator|+=
name|entries
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
return|return
name|total
return|;
block|}
specifier|protected
name|NodeDocument
name|getDocument
parameter_list|(
name|DocumentNodeStore
name|nodeStore
parameter_list|,
name|String
name|path
parameter_list|)
block|{
return|return
name|nodeStore
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
return|;
block|}
specifier|protected
name|TestBuilder
name|newDocumentMKBuilder
parameter_list|()
block|{
return|return
operator|new
name|TestBuilder
argument_list|()
return|;
block|}
specifier|protected
name|DocumentMK
name|createMK
parameter_list|(
name|int
name|clusterId
parameter_list|,
name|int
name|asyncDelay
parameter_list|,
name|DocumentStore
name|ds
parameter_list|,
name|BlobStore
name|bs
parameter_list|)
block|{
name|builder
operator|=
name|newDocumentMKBuilder
argument_list|()
expr_stmt|;
return|return
name|register
argument_list|(
name|builder
operator|.
name|setDocumentStore
argument_list|(
name|ds
argument_list|)
operator|.
name|setBlobStore
argument_list|(
name|bs
argument_list|)
operator|.
name|setClusterId
argument_list|(
name|clusterId
argument_list|)
operator|.
name|setAsyncDelay
argument_list|(
name|asyncDelay
argument_list|)
operator|.
name|open
argument_list|()
argument_list|)
return|;
block|}
specifier|protected
name|DocumentMK
name|register
parameter_list|(
name|DocumentMK
name|mk
parameter_list|)
block|{
name|mks
operator|.
name|add
argument_list|(
name|mk
argument_list|)
expr_stmt|;
return|return
name|mk
return|;
block|}
specifier|protected
specifier|final
class|class
name|TestBuilder
extends|extends
name|DocumentMK
operator|.
name|Builder
block|{
name|CountingDocumentStore
name|actualStore
decl_stmt|;
name|CountingDiffCache
name|actualDiffCache
decl_stmt|;
annotation|@
name|Override
specifier|public
name|DocumentStore
name|getDocumentStore
parameter_list|()
block|{
if|if
condition|(
name|actualStore
operator|==
literal|null
condition|)
block|{
name|actualStore
operator|=
operator|new
name|CountingDocumentStore
argument_list|(
name|super
operator|.
name|getDocumentStore
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|actualStore
return|;
block|}
annotation|@
name|Override
specifier|public
name|DiffCache
name|getDiffCache
parameter_list|()
block|{
if|if
condition|(
name|actualDiffCache
operator|==
literal|null
condition|)
block|{
name|actualDiffCache
operator|=
operator|new
name|CountingDiffCache
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
return|return
name|actualDiffCache
return|;
block|}
block|}
block|}
end_class

end_unit

