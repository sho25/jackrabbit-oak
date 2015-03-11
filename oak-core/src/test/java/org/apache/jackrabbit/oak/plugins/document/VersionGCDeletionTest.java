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
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
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
name|concurrent
operator|.
name|TimeUnit
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
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|stats
operator|.
name|Clock
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
operator|.
name|HOURS
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
name|assertNull
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

begin_class
specifier|public
class|class
name|VersionGCDeletionTest
block|{
specifier|private
name|Clock
name|clock
decl_stmt|;
specifier|private
name|DocumentNodeStore
name|store
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|clock
operator|=
operator|new
name|Clock
operator|.
name|Virtual
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|store
operator|!=
literal|null
condition|)
block|{
name|store
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
name|Revision
operator|.
name|resetClockToDefault
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|deleteParentLast
parameter_list|()
throws|throws
name|Exception
block|{
name|TestDocumentStore
name|ts
init|=
operator|new
name|TestDocumentStore
argument_list|()
decl_stmt|;
name|store
operator|=
operator|new
name|DocumentMK
operator|.
name|Builder
argument_list|()
operator|.
name|clock
argument_list|(
name|clock
argument_list|)
operator|.
name|setDocumentStore
argument_list|(
name|ts
argument_list|)
operator|.
name|setAsyncDelay
argument_list|(
literal|0
argument_list|)
operator|.
name|getNodeStore
argument_list|()
expr_stmt|;
comment|//Baseline the clock
name|clock
operator|.
name|waitUntil
argument_list|(
name|Revision
operator|.
name|getCurrentTimestamp
argument_list|()
argument_list|)
expr_stmt|;
name|NodeBuilder
name|b1
init|=
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|b1
operator|.
name|child
argument_list|(
literal|"x"
argument_list|)
operator|.
name|child
argument_list|(
literal|"y"
argument_list|)
expr_stmt|;
name|store
operator|.
name|merge
argument_list|(
name|b1
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
name|long
name|maxAge
init|=
literal|1
decl_stmt|;
comment|//hours
name|long
name|delta
init|=
name|TimeUnit
operator|.
name|MINUTES
operator|.
name|toMillis
argument_list|(
literal|10
argument_list|)
decl_stmt|;
comment|//Remove x/y
name|NodeBuilder
name|b2
init|=
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|b2
operator|.
name|child
argument_list|(
literal|"x"
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|store
operator|.
name|merge
argument_list|(
name|b2
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
comment|//3. Check that deleted doc does get collected post maxAge
name|clock
operator|.
name|waitUntil
argument_list|(
name|clock
operator|.
name|getTime
argument_list|()
operator|+
name|HOURS
operator|.
name|toMillis
argument_list|(
name|maxAge
operator|*
literal|2
argument_list|)
operator|+
name|delta
argument_list|)
expr_stmt|;
name|VersionGarbageCollector
name|gc
init|=
name|store
operator|.
name|getVersionGarbageCollector
argument_list|()
decl_stmt|;
comment|//4. Ensure that while GC is being run /x gets removed but failure occurs
comment|//for /x/y. At least attempt that! Once issue is fixed the list would be
comment|//sorted again by VersionGC and then /x would always come after /x/y
try|try
block|{
name|ts
operator|.
name|throwException
operator|=
literal|true
expr_stmt|;
name|gc
operator|.
name|gc
argument_list|(
name|maxAge
operator|*
literal|2
argument_list|,
name|HOURS
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Exception should be thrown"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AssertionError
name|ignore
parameter_list|)
block|{          }
name|ts
operator|.
name|throwException
operator|=
literal|false
expr_stmt|;
name|gc
operator|.
name|gc
argument_list|(
name|maxAge
operator|*
literal|2
argument_list|,
name|HOURS
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|ts
operator|.
name|find
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
literal|"2:/x/y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|ts
operator|.
name|find
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
literal|"1:/x"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
class|class
name|TestDocumentStore
extends|extends
name|MemoryDocumentStore
block|{
name|boolean
name|throwException
decl_stmt|;
annotation|@
name|Override
specifier|public
parameter_list|<
name|T
extends|extends
name|Document
parameter_list|>
name|void
name|remove
parameter_list|(
name|Collection
argument_list|<
name|T
argument_list|>
name|collection
parameter_list|,
name|String
name|path
parameter_list|)
block|{
if|if
condition|(
name|throwException
operator|&&
literal|"2:/x/y"
operator|.
name|equals
argument_list|(
name|path
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|()
throw|;
block|}
name|super
operator|.
name|remove
argument_list|(
name|collection
argument_list|,
name|path
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
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
name|List
argument_list|<
name|T
argument_list|>
name|result
init|=
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
decl_stmt|;
comment|//Ensure that /x comes before /x/y
if|if
condition|(
name|NodeDocument
operator|.
name|DELETED_ONCE
operator|.
name|equals
argument_list|(
name|indexedProperty
argument_list|)
condition|)
block|{
name|Collections
operator|.
name|sort
argument_list|(
operator|(
name|List
argument_list|<
name|NodeDocument
argument_list|>
operator|)
name|result
argument_list|,
operator|new
name|NodeDocComparator
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
block|}
comment|/**      * Ensures that NodeDocument with path  /x/y /x/y/z /x get sorted to      * /x /x/y /x/y/z      */
specifier|private
specifier|static
class|class
name|NodeDocComparator
implements|implements
name|Comparator
argument_list|<
name|NodeDocument
argument_list|>
block|{
specifier|private
specifier|static
name|Comparator
argument_list|<
name|String
argument_list|>
name|reverse
init|=
name|Collections
operator|.
name|reverseOrder
argument_list|(
name|PathComparator
operator|.
name|INSTANCE
argument_list|)
decl_stmt|;
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|NodeDocument
name|o1
parameter_list|,
name|NodeDocument
name|o2
parameter_list|)
block|{
return|return
name|reverse
operator|.
name|compare
argument_list|(
name|o1
operator|.
name|getPath
argument_list|()
argument_list|,
name|o2
operator|.
name|getPath
argument_list|()
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

