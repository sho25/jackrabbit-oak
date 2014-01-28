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
name|fail
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
name|HashMap
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
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|mk
operator|.
name|api
operator|.
name|MicroKernelException
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
name|mk
operator|.
name|blobs
operator|.
name|MemoryBlobStore
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
name|mk
operator|.
name|json
operator|.
name|JsonObject
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
name|mk
operator|.
name|json
operator|.
name|JsopBuilder
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
name|mk
operator|.
name|json
operator|.
name|JsopTokenizer
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
import|import
name|com
operator|.
name|mongodb
operator|.
name|DB
import|;
end_import

begin_comment
comment|/**  * A simple randomized dual-instance test.  */
end_comment

begin_class
specifier|public
class|class
name|RandomizedClusterTest
block|{
specifier|private
specifier|static
specifier|final
name|boolean
name|MONGO_DB
init|=
literal|false
decl_stmt|;
comment|// private static final boolean MONGO_DB = true;
specifier|private
specifier|static
specifier|final
name|int
name|MK_COUNT
init|=
literal|2
decl_stmt|;
specifier|private
name|MemoryDocumentStore
name|ds
decl_stmt|;
specifier|private
name|MemoryBlobStore
name|bs
decl_stmt|;
specifier|private
name|DocumentMK
index|[]
name|mkList
init|=
operator|new
name|DocumentMK
index|[
name|MK_COUNT
index|]
decl_stmt|;
specifier|private
name|String
index|[]
name|revList
init|=
operator|new
name|String
index|[
name|MK_COUNT
index|]
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"unchecked"
block|,
literal|"cast"
block|}
argument_list|)
specifier|private
name|HashSet
argument_list|<
name|Integer
argument_list|>
index|[]
name|unseenChanges
init|=
operator|(
name|HashSet
argument_list|<
name|Integer
argument_list|>
index|[]
operator|)
operator|new
name|HashSet
index|[
name|MK_COUNT
index|]
decl_stmt|;
specifier|private
name|HashMap
argument_list|<
name|Integer
argument_list|,
name|List
argument_list|<
name|Op
argument_list|>
argument_list|>
name|changes
init|=
operator|new
name|HashMap
argument_list|<
name|Integer
argument_list|,
name|List
argument_list|<
name|Op
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|int
name|opId
decl_stmt|;
specifier|private
name|int
name|mkId
decl_stmt|;
specifier|private
name|StringBuilder
name|log
decl_stmt|;
comment|/**      * The map of changes. Key: node name; value: the last operation that      * changed the node.      */
specifier|private
name|HashMap
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|nodeChange
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|addRemoveSet
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|MK_COUNT
condition|;
name|i
operator|++
control|)
block|{
name|unseenChanges
index|[
name|i
index|]
operator|=
operator|new
name|HashSet
argument_list|<
name|Integer
argument_list|>
argument_list|()
expr_stmt|;
name|mkList
index|[
name|i
index|]
operator|=
name|createMK
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|revList
index|[
name|i
index|]
operator|=
name|mkList
index|[
name|i
index|]
operator|.
name|getHeadRevision
argument_list|()
expr_stmt|;
block|}
name|HashMap
argument_list|<
name|Integer
argument_list|,
name|ClusterRev
argument_list|>
name|revs
init|=
operator|new
name|HashMap
argument_list|<
name|Integer
argument_list|,
name|ClusterRev
argument_list|>
argument_list|()
decl_stmt|;
name|Random
name|r
init|=
operator|new
name|Random
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|int
name|operations
init|=
literal|1000
decl_stmt|,
name|nodeCount
init|=
literal|10
decl_stmt|;
name|int
name|valueCount
init|=
literal|10
decl_stmt|;
name|int
name|maxBackRev
init|=
literal|20
decl_stmt|;
name|log
operator|=
operator|new
name|StringBuilder
argument_list|()
expr_stmt|;
try|try
block|{
name|int
name|maskOk
init|=
literal|0
decl_stmt|,
name|maskFail
init|=
literal|0
decl_stmt|;
name|int
name|opCount
init|=
literal|6
decl_stmt|;
name|nodeChange
operator|.
name|clear
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|operations
condition|;
name|i
operator|++
control|)
block|{
name|opId
operator|=
name|i
expr_stmt|;
name|mkId
operator|=
name|r
operator|.
name|nextInt
argument_list|(
name|mkList
operator|.
name|length
argument_list|)
expr_stmt|;
name|String
name|node
init|=
literal|"t"
operator|+
name|r
operator|.
name|nextInt
argument_list|(
name|nodeCount
argument_list|)
decl_stmt|;
name|String
name|node2
init|=
literal|"t"
operator|+
name|r
operator|.
name|nextInt
argument_list|(
name|nodeCount
argument_list|)
decl_stmt|;
name|String
name|property
init|=
literal|"x"
decl_stmt|;
name|String
name|value
init|=
literal|""
operator|+
name|r
operator|.
name|nextInt
argument_list|(
name|valueCount
argument_list|)
decl_stmt|;
name|String
name|diff
decl_stmt|;
name|int
name|op
init|=
name|r
operator|.
name|nextInt
argument_list|(
name|opCount
argument_list|)
decl_stmt|;
if|if
condition|(
name|i
operator|<
literal|20
condition|)
block|{
comment|// we need to add many nodes first, so that
comment|// there are enough nodes to operate on
name|op
operator|=
literal|0
expr_stmt|;
block|}
name|String
name|result
decl_stmt|;
name|boolean
name|conflictExpected
decl_stmt|;
switch|switch
condition|(
name|op
condition|)
block|{
case|case
literal|0
case|:
name|diff
operator|=
literal|"+ \""
operator|+
name|node
operator|+
literal|"\": { \""
operator|+
name|property
operator|+
literal|"\": "
operator|+
name|value
operator|+
literal|"}"
expr_stmt|;
name|log
argument_list|(
name|diff
argument_list|)
expr_stmt|;
if|if
condition|(
name|exists
argument_list|(
name|node
argument_list|)
condition|)
block|{
name|log
argument_list|(
literal|"already exists"
argument_list|)
expr_stmt|;
name|result
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|conflictExpected
operator|=
name|isConflict
argument_list|(
name|node
argument_list|)
expr_stmt|;
name|result
operator|=
name|commit
argument_list|(
name|diff
argument_list|,
name|conflictExpected
argument_list|)
expr_stmt|;
if|if
condition|(
name|result
operator|!=
literal|null
condition|)
block|{
name|changes
operator|.
name|put
argument_list|(
name|i
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|Op
argument_list|(
name|mkId
argument_list|,
name|node
argument_list|,
name|value
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|nodeChange
operator|.
name|put
argument_list|(
name|node
argument_list|,
name|i
argument_list|)
expr_stmt|;
block|}
block|}
break|break;
case|case
literal|1
case|:
name|diff
operator|=
literal|"- \""
operator|+
name|node
operator|+
literal|"\""
expr_stmt|;
name|log
argument_list|(
name|diff
argument_list|)
expr_stmt|;
if|if
condition|(
name|exists
argument_list|(
name|node
argument_list|)
condition|)
block|{
name|conflictExpected
operator|=
name|isConflict
argument_list|(
name|node
argument_list|)
expr_stmt|;
name|result
operator|=
name|commit
argument_list|(
name|diff
argument_list|,
name|conflictExpected
argument_list|)
expr_stmt|;
if|if
condition|(
name|result
operator|!=
literal|null
condition|)
block|{
name|changes
operator|.
name|put
argument_list|(
name|i
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|Op
argument_list|(
name|mkId
argument_list|,
name|node
argument_list|,
literal|null
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|nodeChange
operator|.
name|put
argument_list|(
name|node
argument_list|,
name|i
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|log
argument_list|(
literal|"doesn't exist"
argument_list|)
expr_stmt|;
name|result
operator|=
literal|null
expr_stmt|;
block|}
break|break;
case|case
literal|2
case|:
name|diff
operator|=
literal|"^ \""
operator|+
name|node
operator|+
literal|"/"
operator|+
name|property
operator|+
literal|"\": "
operator|+
name|value
expr_stmt|;
name|log
argument_list|(
name|diff
argument_list|)
expr_stmt|;
if|if
condition|(
name|exists
argument_list|(
name|node
argument_list|)
condition|)
block|{
name|conflictExpected
operator|=
name|isConflict
argument_list|(
name|node
argument_list|)
expr_stmt|;
name|result
operator|=
name|commit
argument_list|(
name|diff
argument_list|,
name|conflictExpected
argument_list|)
expr_stmt|;
if|if
condition|(
name|result
operator|!=
literal|null
condition|)
block|{
name|changes
operator|.
name|put
argument_list|(
name|i
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|Op
argument_list|(
name|mkId
argument_list|,
name|node
argument_list|,
name|value
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|nodeChange
operator|.
name|put
argument_list|(
name|node
argument_list|,
name|i
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|log
argument_list|(
literal|"doesn't exist"
argument_list|)
expr_stmt|;
name|result
operator|=
literal|null
expr_stmt|;
block|}
break|break;
case|case
literal|3
case|:
name|diff
operator|=
literal|"> \""
operator|+
name|node
operator|+
literal|"\": \""
operator|+
name|node2
operator|+
literal|"\""
expr_stmt|;
name|log
argument_list|(
name|diff
argument_list|)
expr_stmt|;
if|if
condition|(
name|exists
argument_list|(
name|node
argument_list|)
operator|&&
operator|!
name|exists
argument_list|(
name|node2
argument_list|)
condition|)
block|{
name|conflictExpected
operator|=
name|isConflict
argument_list|(
name|node
argument_list|)
operator||
name|isConflict
argument_list|(
name|node2
argument_list|)
expr_stmt|;
name|result
operator|=
name|commit
argument_list|(
name|diff
argument_list|,
name|conflictExpected
argument_list|)
expr_stmt|;
if|if
condition|(
name|result
operator|!=
literal|null
condition|)
block|{
name|value
operator|=
name|getValue
argument_list|(
name|mkId
argument_list|,
name|i
argument_list|,
name|node
argument_list|)
expr_stmt|;
name|changes
operator|.
name|put
argument_list|(
name|i
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|Op
argument_list|(
name|mkId
argument_list|,
name|node
argument_list|,
literal|null
argument_list|)
argument_list|,
operator|new
name|Op
argument_list|(
name|mkId
argument_list|,
name|node2
argument_list|,
name|value
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|nodeChange
operator|.
name|put
argument_list|(
name|node
argument_list|,
name|i
argument_list|)
expr_stmt|;
name|nodeChange
operator|.
name|put
argument_list|(
name|node2
argument_list|,
name|i
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|log
argument_list|(
literal|"source doesn't exist or target exists"
argument_list|)
expr_stmt|;
name|result
operator|=
literal|null
expr_stmt|;
block|}
break|break;
case|case
literal|4
case|:
if|if
condition|(
name|isConflict
argument_list|(
name|node
argument_list|)
condition|)
block|{
comment|// the MicroKernelImpl would report a conflict
name|result
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|diff
operator|=
literal|"* \""
operator|+
name|node
operator|+
literal|"\": \""
operator|+
name|node2
operator|+
literal|"\""
expr_stmt|;
name|log
argument_list|(
name|diff
argument_list|)
expr_stmt|;
if|if
condition|(
name|exists
argument_list|(
name|node
argument_list|)
operator|&&
operator|!
name|exists
argument_list|(
name|node2
argument_list|)
condition|)
block|{
name|conflictExpected
operator|=
name|isConflict
argument_list|(
name|node2
argument_list|)
expr_stmt|;
name|result
operator|=
name|commit
argument_list|(
name|diff
argument_list|,
name|conflictExpected
argument_list|)
expr_stmt|;
if|if
condition|(
name|result
operator|!=
literal|null
condition|)
block|{
name|value
operator|=
name|getValue
argument_list|(
name|mkId
argument_list|,
name|i
argument_list|,
name|node
argument_list|)
expr_stmt|;
name|changes
operator|.
name|put
argument_list|(
name|i
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|Op
argument_list|(
name|mkId
argument_list|,
name|node2
argument_list|,
name|value
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|nodeChange
operator|.
name|put
argument_list|(
name|node2
argument_list|,
name|i
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|log
argument_list|(
literal|"source doesn't exist or target exists"
argument_list|)
expr_stmt|;
name|result
operator|=
literal|null
expr_stmt|;
block|}
block|}
break|break;
case|case
literal|5
case|:
name|log
argument_list|(
literal|"sync/refresh"
argument_list|)
expr_stmt|;
name|syncAndRefreshAllClusterNodes
argument_list|()
expr_stmt|;
comment|// go to head revision
name|result
operator|=
name|revList
index|[
name|mkId
index|]
operator|=
name|mkList
index|[
name|mkId
index|]
operator|.
name|getHeadRevision
argument_list|()
expr_stmt|;
comment|// fake failure
name|maskFail
operator||=
literal|1
operator|<<
name|op
expr_stmt|;
break|break;
default|default:
name|fail
argument_list|()
expr_stmt|;
name|result
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|result
operator|==
literal|null
condition|)
block|{
name|maskFail
operator||=
literal|1
operator|<<
name|op
expr_stmt|;
name|log
argument_list|(
literal|" -> fail "
operator|+
name|Integer
operator|.
name|toBinaryString
argument_list|(
name|maskFail
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|maskOk
operator||=
literal|1
operator|<<
name|op
expr_stmt|;
name|log
argument_list|(
literal|" -> "
operator|+
name|result
argument_list|)
expr_stmt|;
comment|// all other cluster nodes didn't see this particular change yet
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|unseenChanges
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
if|if
condition|(
name|j
operator|!=
name|mkId
condition|)
block|{
name|unseenChanges
index|[
name|j
index|]
operator|.
name|add
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|log
argument_list|(
literal|"get "
operator|+
name|node
argument_list|)
expr_stmt|;
name|boolean
name|x
init|=
name|get
argument_list|(
name|i
argument_list|,
name|node
argument_list|)
decl_stmt|;
name|log
argument_list|(
literal|"get "
operator|+
name|node
operator|+
literal|" returns "
operator|+
name|x
argument_list|)
expr_stmt|;
name|log
argument_list|(
literal|"get "
operator|+
name|node2
argument_list|)
expr_stmt|;
name|x
operator|=
name|get
argument_list|(
name|i
argument_list|,
name|node2
argument_list|)
expr_stmt|;
name|log
argument_list|(
literal|"get "
operator|+
name|node2
operator|+
literal|" returns "
operator|+
name|x
argument_list|)
expr_stmt|;
name|DocumentMK
name|mk
init|=
name|mkList
index|[
name|mkId
index|]
decl_stmt|;
name|ClusterRev
name|cr
init|=
operator|new
name|ClusterRev
argument_list|()
decl_stmt|;
name|cr
operator|.
name|mkId
operator|=
name|mkId
expr_stmt|;
name|cr
operator|.
name|rev
operator|=
name|mk
operator|.
name|getHeadRevision
argument_list|()
expr_stmt|;
name|revs
operator|.
name|put
argument_list|(
name|i
argument_list|,
name|cr
argument_list|)
expr_stmt|;
name|revs
operator|.
name|remove
argument_list|(
name|i
operator|-
name|maxBackRev
argument_list|)
expr_stmt|;
name|log
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|Integer
operator|.
name|bitCount
argument_list|(
name|maskOk
argument_list|)
operator|!=
name|opCount
condition|)
block|{
name|fail
argument_list|(
literal|"Not all operations were at least once successful: "
operator|+
name|Integer
operator|.
name|toBinaryString
argument_list|(
name|maskOk
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|Integer
operator|.
name|bitCount
argument_list|(
name|maskFail
argument_list|)
operator|!=
name|opCount
condition|)
block|{
name|fail
argument_list|(
literal|"Not all operations failed at least once: "
operator|+
name|Integer
operator|.
name|toBinaryString
argument_list|(
name|maskFail
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|AssertionError
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|Exception
argument_list|(
literal|"log: "
operator|+
name|log
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|Exception
argument_list|(
literal|"log: "
operator|+
name|log
argument_list|,
name|e
argument_list|)
throw|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|MK_COUNT
condition|;
name|i
operator|++
control|)
block|{
name|mkList
index|[
name|i
index|]
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
comment|// System.out.println(log);
comment|// System.out.println();
block|}
specifier|private
name|String
name|getValue
parameter_list|(
name|int
name|clusterId
parameter_list|,
name|int
name|maxOp
parameter_list|,
name|String
name|nodeName
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
name|maxOp
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|--
control|)
block|{
name|List
argument_list|<
name|Op
argument_list|>
name|ops
init|=
name|changes
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|ops
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Op
name|o
range|:
name|ops
control|)
block|{
if|if
condition|(
name|o
operator|.
name|clusterId
operator|!=
name|clusterId
operator|&&
name|unseenChanges
index|[
name|clusterId
index|]
operator|.
name|contains
argument_list|(
name|i
argument_list|)
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|o
operator|.
name|nodeName
operator|.
name|equals
argument_list|(
name|nodeName
argument_list|)
condition|)
block|{
return|return
name|o
operator|.
name|value
return|;
block|}
block|}
block|}
block|}
return|return
literal|null
return|;
block|}
specifier|private
name|boolean
name|isConflict
parameter_list|(
name|String
name|node
parameter_list|)
block|{
name|Integer
name|change
init|=
name|nodeChange
operator|.
name|get
argument_list|(
name|node
argument_list|)
decl_stmt|;
if|if
condition|(
name|change
operator|==
literal|null
operator|||
operator|!
name|unseenChanges
index|[
name|mkId
index|]
operator|.
name|contains
argument_list|(
name|change
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
specifier|private
name|void
name|log
parameter_list|(
name|String
name|msg
parameter_list|)
block|{
name|msg
operator|=
name|opId
operator|+
literal|": ["
operator|+
name|mkId
operator|+
literal|"] "
operator|+
name|msg
operator|+
literal|"\n"
expr_stmt|;
name|log
operator|.
name|append
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|syncClusterNode
parameter_list|()
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|mkList
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|DocumentMK
name|mk
init|=
name|mkList
index|[
name|i
index|]
decl_stmt|;
name|mk
operator|.
name|backgroundWrite
argument_list|()
expr_stmt|;
block|}
name|DocumentMK
name|mk
init|=
name|mkList
index|[
name|mkId
index|]
decl_stmt|;
name|mk
operator|.
name|backgroundRead
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|syncAndRefreshAllClusterNodes
parameter_list|()
block|{
name|syncClusterNode
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|mkList
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|DocumentMK
name|mk
init|=
name|mkList
index|[
name|i
index|]
decl_stmt|;
name|mk
operator|.
name|backgroundRead
argument_list|()
expr_stmt|;
name|revList
index|[
name|i
index|]
operator|=
name|mk
operator|.
name|getHeadRevision
argument_list|()
expr_stmt|;
name|unseenChanges
index|[
name|i
index|]
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
name|log
argument_list|(
literal|"sync"
argument_list|)
expr_stmt|;
block|}
specifier|private
name|boolean
name|get
parameter_list|(
name|int
name|maxOp
parameter_list|,
name|String
name|node
parameter_list|)
block|{
name|String
name|head
init|=
name|revList
index|[
name|mkId
index|]
decl_stmt|;
return|return
name|get
argument_list|(
name|maxOp
argument_list|,
name|node
argument_list|,
name|head
argument_list|)
return|;
block|}
specifier|private
name|boolean
name|exists
parameter_list|(
name|String
name|node
parameter_list|)
block|{
name|String
name|head
init|=
name|revList
index|[
name|mkId
index|]
decl_stmt|;
name|DocumentMK
name|mk
init|=
name|mkList
index|[
name|mkId
index|]
decl_stmt|;
return|return
name|mk
operator|.
name|nodeExists
argument_list|(
literal|"/"
operator|+
name|node
argument_list|,
name|head
argument_list|)
return|;
block|}
specifier|private
name|boolean
name|get
parameter_list|(
name|int
name|maxOp
parameter_list|,
name|String
name|node
parameter_list|,
name|String
name|head
parameter_list|)
block|{
name|String
name|p
init|=
literal|"/"
operator|+
name|node
decl_stmt|;
name|DocumentMK
name|mk
init|=
name|mkList
index|[
name|mkId
index|]
decl_stmt|;
name|String
name|value
init|=
name|getValue
argument_list|(
name|mkId
argument_list|,
name|maxOp
argument_list|,
name|node
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
name|assertFalse
argument_list|(
literal|"path: "
operator|+
name|p
operator|+
literal|" is supposed to not exist"
argument_list|,
name|mk
operator|.
name|nodeExists
argument_list|(
name|p
argument_list|,
name|head
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
if|if
condition|(
operator|!
name|mk
operator|.
name|nodeExists
argument_list|(
name|p
argument_list|,
name|head
argument_list|)
condition|)
block|{
name|assertTrue
argument_list|(
literal|"path: "
operator|+
name|p
operator|+
literal|" is supposed to exist"
argument_list|,
name|mk
operator|.
name|nodeExists
argument_list|(
name|p
argument_list|,
name|head
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|String
name|expected
init|=
literal|"{\":childNodeCount\":0,\"x\":"
operator|+
name|value
operator|+
literal|"}"
decl_stmt|;
name|String
name|result
init|=
name|mk
operator|.
name|getNodes
argument_list|(
name|p
argument_list|,
name|head
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|expected
operator|=
name|normalize
argument_list|(
name|expected
argument_list|)
expr_stmt|;
name|result
operator|=
name|normalize
argument_list|(
name|result
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|result
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
specifier|private
specifier|static
name|String
name|normalize
parameter_list|(
name|String
name|json
parameter_list|)
block|{
name|JsopTokenizer
name|t
init|=
operator|new
name|JsopTokenizer
argument_list|(
name|json
argument_list|)
decl_stmt|;
name|t
operator|.
name|read
argument_list|(
literal|'{'
argument_list|)
expr_stmt|;
name|JsonObject
name|o
init|=
name|JsonObject
operator|.
name|create
argument_list|(
name|t
argument_list|)
decl_stmt|;
name|JsopBuilder
name|w
init|=
operator|new
name|JsopBuilder
argument_list|()
decl_stmt|;
name|o
operator|.
name|toJson
argument_list|(
name|w
argument_list|)
expr_stmt|;
return|return
name|w
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|private
name|String
name|commit
parameter_list|(
name|String
name|diff
parameter_list|,
name|boolean
name|conflictExpected
parameter_list|)
block|{
name|boolean
name|ok
init|=
literal|false
decl_stmt|;
name|DocumentMK
name|mk
init|=
name|mkList
index|[
name|mkId
index|]
decl_stmt|;
name|String
name|rev
init|=
name|revList
index|[
name|mkId
index|]
decl_stmt|;
name|String
name|result
init|=
literal|null
decl_stmt|;
name|String
name|ex
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|conflictExpected
condition|)
block|{
name|ok
operator|=
literal|false
expr_stmt|;
name|ex
operator|=
literal|"conflict expected"
expr_stmt|;
block|}
else|else
block|{
name|ok
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|ok
condition|)
block|{
name|result
operator|=
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
name|diff
argument_list|,
name|rev
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|revList
index|[
name|mkId
index|]
operator|=
name|result
expr_stmt|;
block|}
else|else
block|{
comment|// System.out.println("--> fail " + e.toString());
try|try
block|{
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
name|diff
argument_list|,
name|rev
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Should fail: "
operator|+
name|diff
operator|+
literal|" with "
operator|+
name|ex
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MicroKernelException
name|e2
parameter_list|)
block|{
comment|// expected
name|revList
index|[
name|mkId
index|]
operator|=
name|mk
operator|.
name|getHeadRevision
argument_list|()
expr_stmt|;
comment|// it might have been not a conflict with another cluster node
comment|// TODO test two cases: conflict with other cluster node
comment|// (this should auto-synchronize until the given conflict)
comment|// and conflict with a previous change that was already seen,
comment|// which shouldn't synchronize
name|syncAndRefreshAllClusterNodes
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
specifier|private
name|DocumentMK
name|createMK
parameter_list|(
name|int
name|clusterId
parameter_list|)
block|{
name|DocumentMK
operator|.
name|Builder
name|builder
init|=
operator|new
name|DocumentMK
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setAsyncDelay
argument_list|(
literal|0
argument_list|)
expr_stmt|;
if|if
condition|(
name|MONGO_DB
condition|)
block|{
name|DB
name|db
init|=
name|MongoUtils
operator|.
name|getConnection
argument_list|()
operator|.
name|getDB
argument_list|()
decl_stmt|;
name|MongoUtils
operator|.
name|dropCollections
argument_list|(
name|db
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setMongoDB
argument_list|(
name|db
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|ds
operator|==
literal|null
condition|)
block|{
name|ds
operator|=
operator|new
name|MemoryDocumentStore
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|bs
operator|==
literal|null
condition|)
block|{
name|bs
operator|=
operator|new
name|MemoryBlobStore
argument_list|()
expr_stmt|;
block|}
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
expr_stmt|;
block|}
return|return
name|builder
operator|.
name|setClusterId
argument_list|(
name|clusterId
operator|+
literal|1
argument_list|)
operator|.
name|open
argument_list|()
return|;
block|}
comment|/**      * A revision in a certain cluster node.      */
specifier|static
class|class
name|ClusterRev
block|{
name|int
name|mkId
decl_stmt|;
name|String
name|rev
decl_stmt|;
block|}
comment|/**      * An operation.      */
specifier|static
class|class
name|Op
block|{
specifier|final
name|int
name|clusterId
decl_stmt|;
specifier|final
name|String
name|nodeName
decl_stmt|;
specifier|final
name|String
name|value
decl_stmt|;
specifier|public
name|Op
parameter_list|(
name|int
name|clusterId
parameter_list|,
name|String
name|nodeName
parameter_list|,
name|String
name|value
parameter_list|)
block|{
name|this
operator|.
name|clusterId
operator|=
name|clusterId
expr_stmt|;
name|this
operator|.
name|nodeName
operator|=
name|nodeName
expr_stmt|;
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

