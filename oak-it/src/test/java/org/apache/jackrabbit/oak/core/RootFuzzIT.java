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
name|core
package|;
end_package

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
name|core
operator|.
name|RootFuzzIT
operator|.
name|Operation
operator|.
name|AddNode
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
name|core
operator|.
name|RootFuzzIT
operator|.
name|Operation
operator|.
name|MoveNode
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
name|core
operator|.
name|RootFuzzIT
operator|.
name|Operation
operator|.
name|RemoveNode
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
name|core
operator|.
name|RootFuzzIT
operator|.
name|Operation
operator|.
name|RemoveProperty
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
name|core
operator|.
name|RootFuzzIT
operator|.
name|Operation
operator|.
name|Save
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
name|core
operator|.
name|RootFuzzIT
operator|.
name|Operation
operator|.
name|SetProperty
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
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|EnumSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|oak
operator|.
name|NodeStoreFixtures
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
name|api
operator|.
name|PropertyState
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
name|api
operator|.
name|Tree
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
name|FixturesHelper
operator|.
name|Fixture
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
name|core
operator|.
name|RootFuzzIT
operator|.
name|Operation
operator|.
name|Rebase
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
name|fixture
operator|.
name|NodeStoreFixture
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
name|tree
operator|.
name|factories
operator|.
name|RootFactory
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
import|import
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|RunWith
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|Parameterized
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|Parameterized
operator|.
name|Parameters
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

begin_comment
comment|/**  * Fuzz test running random sequences of operations on {@link Tree}.  * Run with -DRootFuzzIT-seed=42 to set a specific seed (i.e. 42);  */
end_comment

begin_class
annotation|@
name|RunWith
argument_list|(
name|value
operator|=
name|Parameterized
operator|.
name|class
argument_list|)
specifier|public
class|class
name|RootFuzzIT
block|{
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|RootFuzzIT
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Parameters
argument_list|(
name|name
operator|=
literal|"{0}"
argument_list|)
specifier|public
specifier|static
name|Collection
argument_list|<
name|Object
index|[]
argument_list|>
name|fixtures
parameter_list|()
block|{
return|return
name|NodeStoreFixtures
operator|.
name|asJunitParameters
argument_list|(
name|EnumSet
operator|.
name|of
argument_list|(
name|Fixture
operator|.
name|DOCUMENT_NS
argument_list|,
name|Fixture
operator|.
name|SEGMENT_TAR
argument_list|)
argument_list|)
return|;
block|}
specifier|private
specifier|static
specifier|final
name|int
name|OP_COUNT
init|=
literal|5000
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|SEED
init|=
name|Integer
operator|.
name|getInteger
argument_list|(
name|RootFuzzIT
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"-seed"
argument_list|,
operator|new
name|Random
argument_list|()
operator|.
name|nextInt
argument_list|()
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Random
name|random
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|NodeStoreFixture
name|fixture
decl_stmt|;
specifier|private
name|NodeStore
name|store1
decl_stmt|;
specifier|private
name|Root
name|root1
decl_stmt|;
specifier|private
name|NodeStore
name|store2
decl_stmt|;
specifier|private
name|Root
name|root2
decl_stmt|;
specifier|private
name|int
name|counter
decl_stmt|;
specifier|public
name|RootFuzzIT
parameter_list|(
name|NodeStoreFixture
name|fixture
parameter_list|)
block|{
name|this
operator|.
name|fixture
operator|=
name|fixture
expr_stmt|;
block|}
annotation|@
name|Before
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Running "
operator|+
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|" with "
operator|+
name|fixture
operator|+
literal|" and seed "
operator|+
name|SEED
argument_list|)
expr_stmt|;
name|random
operator|.
name|setSeed
argument_list|(
name|SEED
argument_list|)
expr_stmt|;
name|counter
operator|=
literal|0
expr_stmt|;
name|store1
operator|=
name|fixture
operator|.
name|createNodeStore
argument_list|()
expr_stmt|;
name|root1
operator|=
name|RootFactory
operator|.
name|createSystemRoot
argument_list|(
name|store1
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|root1
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
operator|.
name|addChild
argument_list|(
literal|"root"
argument_list|)
expr_stmt|;
name|root1
operator|.
name|commit
argument_list|()
expr_stmt|;
name|store2
operator|=
name|fixture
operator|.
name|createNodeStore
argument_list|()
expr_stmt|;
name|root2
operator|=
name|RootFactory
operator|.
name|createSystemRoot
argument_list|(
name|store2
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|root2
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
operator|.
name|addChild
argument_list|(
literal|"root"
argument_list|)
expr_stmt|;
name|root2
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|teardown
parameter_list|()
block|{
name|fixture
operator|.
name|dispose
argument_list|(
name|store1
argument_list|)
expr_stmt|;
name|fixture
operator|.
name|dispose
argument_list|(
name|store2
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|fuzzTest
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|Operation
name|op
range|:
name|operations
argument_list|(
name|OP_COUNT
argument_list|)
control|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"{}"
argument_list|,
name|op
argument_list|)
expr_stmt|;
name|op
operator|.
name|apply
argument_list|(
name|root1
argument_list|)
expr_stmt|;
name|op
operator|.
name|apply
argument_list|(
name|root2
argument_list|)
expr_stmt|;
name|checkEqual
argument_list|(
name|root1
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
argument_list|,
name|root2
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
argument_list|)
expr_stmt|;
name|root1
operator|.
name|commit
argument_list|()
expr_stmt|;
name|checkEqual
argument_list|(
name|root1
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
argument_list|,
name|root2
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|op
operator|instanceof
name|Save
condition|)
block|{
name|root2
operator|.
name|commit
argument_list|()
expr_stmt|;
name|checkEqual
argument_list|(
name|root1
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
argument_list|,
name|root2
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|Iterable
argument_list|<
name|Operation
argument_list|>
name|operations
parameter_list|(
specifier|final
name|int
name|count
parameter_list|)
block|{
return|return
operator|new
name|Iterable
argument_list|<
name|Operation
argument_list|>
argument_list|()
block|{
name|int
name|k
init|=
name|count
decl_stmt|;
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|Operation
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|Iterator
argument_list|<
name|Operation
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|k
operator|--
operator|>
literal|0
return|;
block|}
annotation|@
name|Override
specifier|public
name|Operation
name|next
parameter_list|()
block|{
return|return
name|createOperation
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
return|;
block|}
block|}
return|;
block|}
specifier|abstract
specifier|static
class|class
name|Operation
block|{
specifier|abstract
name|void
name|apply
parameter_list|(
name|Root
name|root
parameter_list|)
function_decl|;
specifier|static
class|class
name|AddNode
extends|extends
name|Operation
block|{
specifier|private
specifier|final
name|String
name|parentPath
decl_stmt|;
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
name|AddNode
parameter_list|(
name|String
name|parentPath
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|parentPath
operator|=
name|parentPath
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
annotation|@
name|Override
name|void
name|apply
parameter_list|(
name|Root
name|root
parameter_list|)
block|{
name|root
operator|.
name|getTree
argument_list|(
name|parentPath
argument_list|)
operator|.
name|addChild
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|'+'
operator|+
name|PathUtils
operator|.
name|concat
argument_list|(
name|parentPath
argument_list|,
name|name
argument_list|)
operator|+
literal|":{}"
return|;
block|}
block|}
specifier|static
class|class
name|RemoveNode
extends|extends
name|Operation
block|{
specifier|private
specifier|final
name|String
name|path
decl_stmt|;
name|RemoveNode
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
block|}
annotation|@
name|Override
name|void
name|apply
parameter_list|(
name|Root
name|root
parameter_list|)
block|{
name|String
name|parentPath
init|=
name|PathUtils
operator|.
name|getParentPath
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|String
name|name
init|=
name|PathUtils
operator|.
name|getName
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|root
operator|.
name|getTree
argument_list|(
name|parentPath
argument_list|)
operator|.
name|getChild
argument_list|(
name|name
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|'-'
operator|+
name|path
return|;
block|}
block|}
specifier|static
class|class
name|MoveNode
extends|extends
name|Operation
block|{
specifier|private
specifier|final
name|String
name|source
decl_stmt|;
specifier|private
specifier|final
name|String
name|destination
decl_stmt|;
name|MoveNode
parameter_list|(
name|String
name|source
parameter_list|,
name|String
name|destParent
parameter_list|,
name|String
name|destName
parameter_list|)
block|{
name|this
operator|.
name|source
operator|=
name|source
expr_stmt|;
name|destination
operator|=
name|PathUtils
operator|.
name|concat
argument_list|(
name|destParent
argument_list|,
name|destName
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
name|void
name|apply
parameter_list|(
name|Root
name|root
parameter_list|)
block|{
name|root
operator|.
name|move
argument_list|(
name|source
argument_list|,
name|destination
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|'>'
operator|+
name|source
operator|+
literal|':'
operator|+
name|destination
return|;
block|}
block|}
specifier|static
class|class
name|SetProperty
extends|extends
name|Operation
block|{
specifier|private
specifier|final
name|String
name|parentPath
decl_stmt|;
specifier|private
specifier|final
name|String
name|propertyName
decl_stmt|;
specifier|private
specifier|final
name|String
name|propertyValue
decl_stmt|;
name|SetProperty
parameter_list|(
name|String
name|parentPath
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|value
parameter_list|)
block|{
name|this
operator|.
name|parentPath
operator|=
name|parentPath
expr_stmt|;
name|this
operator|.
name|propertyName
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|propertyValue
operator|=
name|value
expr_stmt|;
block|}
annotation|@
name|Override
name|void
name|apply
parameter_list|(
name|Root
name|root
parameter_list|)
block|{
name|root
operator|.
name|getTree
argument_list|(
name|parentPath
argument_list|)
operator|.
name|setProperty
argument_list|(
name|propertyName
argument_list|,
name|propertyValue
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|'^'
operator|+
name|PathUtils
operator|.
name|concat
argument_list|(
name|parentPath
argument_list|,
name|propertyName
argument_list|)
operator|+
literal|':'
operator|+
name|propertyValue
return|;
block|}
block|}
specifier|static
class|class
name|RemoveProperty
extends|extends
name|Operation
block|{
specifier|private
specifier|final
name|String
name|parentPath
decl_stmt|;
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
name|RemoveProperty
parameter_list|(
name|String
name|parentPath
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|parentPath
operator|=
name|parentPath
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
annotation|@
name|Override
name|void
name|apply
parameter_list|(
name|Root
name|root
parameter_list|)
block|{
name|root
operator|.
name|getTree
argument_list|(
name|parentPath
argument_list|)
operator|.
name|removeProperty
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|'^'
operator|+
name|PathUtils
operator|.
name|concat
argument_list|(
name|parentPath
argument_list|,
name|name
argument_list|)
operator|+
literal|":null"
return|;
block|}
block|}
specifier|static
class|class
name|Save
extends|extends
name|Operation
block|{
annotation|@
name|Override
name|void
name|apply
parameter_list|(
name|Root
name|root
parameter_list|)
block|{
comment|// empty
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"save"
return|;
block|}
block|}
specifier|static
class|class
name|Rebase
extends|extends
name|Operation
block|{
annotation|@
name|Override
name|void
name|apply
parameter_list|(
name|Root
name|root
parameter_list|)
block|{
name|root
operator|.
name|rebase
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"rebase"
return|;
block|}
block|}
block|}
specifier|private
name|Operation
name|createOperation
parameter_list|()
block|{
name|Operation
name|op
decl_stmt|;
do|do
block|{
switch|switch
condition|(
name|random
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
condition|)
block|{
case|case
literal|0
case|:
case|case
literal|1
case|:
case|case
literal|2
case|:
name|op
operator|=
name|createAddNode
argument_list|()
expr_stmt|;
break|break;
case|case
literal|3
case|:
name|op
operator|=
name|createRemoveNode
argument_list|()
expr_stmt|;
break|break;
case|case
literal|4
case|:
name|op
operator|=
name|createMoveNode
argument_list|()
expr_stmt|;
break|break;
case|case
literal|5
case|:
name|op
operator|=
name|createAddProperty
argument_list|()
expr_stmt|;
break|break;
case|case
literal|6
case|:
name|op
operator|=
name|createSetProperty
argument_list|()
expr_stmt|;
break|break;
case|case
literal|7
case|:
name|op
operator|=
name|createRemoveProperty
argument_list|()
expr_stmt|;
break|break;
case|case
literal|8
case|:
name|op
operator|=
operator|new
name|Save
argument_list|()
expr_stmt|;
break|break;
case|case
literal|9
case|:
name|op
operator|=
operator|new
name|Rebase
argument_list|()
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|IllegalStateException
argument_list|()
throw|;
block|}
block|}
do|while
condition|(
name|op
operator|==
literal|null
condition|)
do|;
return|return
name|op
return|;
block|}
specifier|private
name|Operation
name|createAddNode
parameter_list|()
block|{
name|String
name|parentPath
init|=
name|chooseNodePath
argument_list|()
decl_stmt|;
name|String
name|name
init|=
name|createNodeName
argument_list|()
decl_stmt|;
return|return
operator|new
name|AddNode
argument_list|(
name|parentPath
argument_list|,
name|name
argument_list|)
return|;
block|}
specifier|private
name|Operation
name|createRemoveNode
parameter_list|()
block|{
name|String
name|path
init|=
name|chooseNodePath
argument_list|()
decl_stmt|;
return|return
literal|"/root"
operator|.
name|equals
argument_list|(
name|path
argument_list|)
condition|?
literal|null
else|:
operator|new
name|RemoveNode
argument_list|(
name|path
argument_list|)
return|;
block|}
specifier|private
name|Operation
name|createMoveNode
parameter_list|()
block|{
name|String
name|source
init|=
name|chooseNodePath
argument_list|()
decl_stmt|;
name|String
name|destParent
init|=
name|chooseNodePath
argument_list|()
decl_stmt|;
name|String
name|destName
init|=
name|createNodeName
argument_list|()
decl_stmt|;
return|return
literal|"/root"
operator|.
name|equals
argument_list|(
name|source
argument_list|)
operator|||
name|destParent
operator|.
name|startsWith
argument_list|(
name|source
argument_list|)
condition|?
literal|null
else|:
operator|new
name|MoveNode
argument_list|(
name|source
argument_list|,
name|destParent
argument_list|,
name|destName
argument_list|)
return|;
block|}
specifier|private
name|Operation
name|createAddProperty
parameter_list|()
block|{
name|String
name|parent
init|=
name|chooseNodePath
argument_list|()
decl_stmt|;
name|String
name|name
init|=
name|createPropertyName
argument_list|()
decl_stmt|;
name|String
name|value
init|=
name|createValue
argument_list|()
decl_stmt|;
return|return
operator|new
name|SetProperty
argument_list|(
name|parent
argument_list|,
name|name
argument_list|,
name|value
argument_list|)
return|;
block|}
specifier|private
name|Operation
name|createSetProperty
parameter_list|()
block|{
name|String
name|path
init|=
name|choosePropertyPath
argument_list|()
decl_stmt|;
if|if
condition|(
name|path
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|String
name|value
init|=
name|createValue
argument_list|()
decl_stmt|;
return|return
operator|new
name|SetProperty
argument_list|(
name|PathUtils
operator|.
name|getParentPath
argument_list|(
name|path
argument_list|)
argument_list|,
name|PathUtils
operator|.
name|getName
argument_list|(
name|path
argument_list|)
argument_list|,
name|value
argument_list|)
return|;
block|}
specifier|private
name|Operation
name|createRemoveProperty
parameter_list|()
block|{
name|String
name|path
init|=
name|choosePropertyPath
argument_list|()
decl_stmt|;
if|if
condition|(
name|path
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
operator|new
name|RemoveProperty
argument_list|(
name|PathUtils
operator|.
name|getParentPath
argument_list|(
name|path
argument_list|)
argument_list|,
name|PathUtils
operator|.
name|getName
argument_list|(
name|path
argument_list|)
argument_list|)
return|;
block|}
specifier|private
name|String
name|createNodeName
parameter_list|()
block|{
return|return
literal|"N"
operator|+
name|counter
operator|++
return|;
block|}
specifier|private
name|String
name|createPropertyName
parameter_list|()
block|{
return|return
literal|"P"
operator|+
name|counter
operator|++
return|;
block|}
specifier|private
name|String
name|chooseNodePath
parameter_list|()
block|{
name|String
name|path
init|=
literal|"/root"
decl_stmt|;
name|String
name|next
decl_stmt|;
while|while
condition|(
operator|(
name|next
operator|=
name|chooseNode
argument_list|(
name|path
argument_list|)
operator|)
operator|!=
literal|null
condition|)
block|{
name|path
operator|=
name|next
expr_stmt|;
block|}
return|return
name|path
return|;
block|}
specifier|private
name|String
name|choosePropertyPath
parameter_list|()
block|{
return|return
name|chooseProperty
argument_list|(
name|chooseNodePath
argument_list|()
argument_list|)
return|;
block|}
specifier|private
name|String
name|chooseNode
parameter_list|(
name|String
name|parentPath
parameter_list|)
block|{
name|Tree
name|state
init|=
name|root1
operator|.
name|getTree
argument_list|(
name|parentPath
argument_list|)
decl_stmt|;
name|int
name|k
init|=
name|random
operator|.
name|nextInt
argument_list|(
call|(
name|int
call|)
argument_list|(
name|state
operator|.
name|getChildrenCount
argument_list|(
name|Long
operator|.
name|MAX_VALUE
argument_list|)
operator|+
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|c
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Tree
name|child
range|:
name|state
operator|.
name|getChildren
argument_list|()
control|)
block|{
if|if
condition|(
name|c
operator|++
operator|==
name|k
condition|)
block|{
return|return
name|PathUtils
operator|.
name|concat
argument_list|(
name|parentPath
argument_list|,
name|child
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
specifier|private
name|String
name|chooseProperty
parameter_list|(
name|String
name|parentPath
parameter_list|)
block|{
name|Tree
name|state
init|=
name|root1
operator|.
name|getTree
argument_list|(
name|parentPath
argument_list|)
decl_stmt|;
name|int
name|k
init|=
name|random
operator|.
name|nextInt
argument_list|(
call|(
name|int
call|)
argument_list|(
name|state
operator|.
name|getPropertyCount
argument_list|()
operator|+
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|c
init|=
literal|0
decl_stmt|;
for|for
control|(
name|PropertyState
name|entry
range|:
name|state
operator|.
name|getProperties
argument_list|()
control|)
block|{
if|if
condition|(
name|c
operator|++
operator|==
name|k
condition|)
block|{
return|return
name|PathUtils
operator|.
name|concat
argument_list|(
name|parentPath
argument_list|,
name|entry
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
specifier|private
name|String
name|createValue
parameter_list|()
block|{
return|return
operator|(
literal|"V"
operator|+
name|counter
operator|++
operator|)
return|;
block|}
specifier|private
specifier|static
name|void
name|checkEqual
parameter_list|(
name|Tree
name|tree1
parameter_list|,
name|Tree
name|tree2
parameter_list|)
block|{
name|String
name|message
init|=
name|tree1
operator|.
name|getPath
argument_list|()
operator|+
literal|"!="
operator|+
name|tree2
operator|.
name|getPath
argument_list|()
operator|+
literal|" (seed "
operator|+
name|SEED
operator|+
literal|')'
decl_stmt|;
name|assertEquals
argument_list|(
name|message
argument_list|,
name|tree1
operator|.
name|getPath
argument_list|()
argument_list|,
name|tree2
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|message
argument_list|,
name|tree1
operator|.
name|getChildrenCount
argument_list|(
name|Long
operator|.
name|MAX_VALUE
argument_list|)
argument_list|,
name|tree2
operator|.
name|getChildrenCount
argument_list|(
name|Long
operator|.
name|MAX_VALUE
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|message
argument_list|,
name|tree1
operator|.
name|getPropertyCount
argument_list|()
argument_list|,
name|tree2
operator|.
name|getPropertyCount
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|PropertyState
name|property1
range|:
name|tree1
operator|.
name|getProperties
argument_list|()
control|)
block|{
name|PropertyState
name|property2
init|=
name|tree2
operator|.
name|getProperty
argument_list|(
name|property1
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|message
argument_list|,
name|property1
argument_list|,
name|property2
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Tree
name|child1
range|:
name|tree1
operator|.
name|getChildren
argument_list|()
control|)
block|{
name|checkEqual
argument_list|(
name|child1
argument_list|,
name|tree2
operator|.
name|getChild
argument_list|(
name|child1
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

