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
name|kernel
package|;
end_package

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
name|MicroKernel
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
name|simple
operator|.
name|SimpleKernelImpl
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
name|util
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
name|api
operator|.
name|CoreValue
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
name|CoreValueFactory
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
name|core
operator|.
name|CoreValueFactoryImpl
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
name|Iterator
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
import|import static
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|kernel
operator|.
name|KernelRootFuzzIT
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
name|kernel
operator|.
name|KernelRootFuzzIT
operator|.
name|Operation
operator|.
name|CopyNode
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
name|kernel
operator|.
name|KernelRootFuzzIT
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
name|kernel
operator|.
name|KernelRootFuzzIT
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
name|kernel
operator|.
name|KernelRootFuzzIT
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
name|kernel
operator|.
name|KernelRootFuzzIT
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
name|kernel
operator|.
name|KernelRootFuzzIT
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

begin_class
annotation|@
name|RunWith
argument_list|(
name|Parameterized
operator|.
name|class
argument_list|)
specifier|public
class|class
name|KernelRootFuzzIT
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
name|KernelRootFuzzIT
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|OP_COUNT
init|=
literal|5000
decl_stmt|;
specifier|private
specifier|final
name|Random
name|random
decl_stmt|;
specifier|private
name|KernelNodeStore
name|store1
decl_stmt|;
specifier|private
name|KernelRoot
name|root1
decl_stmt|;
specifier|private
name|KernelNodeStore
name|store2
decl_stmt|;
specifier|private
name|KernelRoot
name|root2
decl_stmt|;
specifier|private
name|int
name|counter
decl_stmt|;
specifier|private
name|CoreValueFactory
name|vf
decl_stmt|;
annotation|@
name|Parameters
specifier|public
specifier|static
name|List
argument_list|<
name|Object
index|[]
argument_list|>
name|seeds
parameter_list|()
block|{
comment|// todo use random sees, log seed, provide means to start with specific seed
return|return
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|Object
index|[]
index|[]
block|{
block|{
literal|0
block|}
block|,
block|{
literal|1
block|}
block|,
block|{
literal|2
block|}
block|,
block|{
literal|3
block|}
block|,
block|{
literal|4
block|}
block|,
block|{
literal|5
block|}
block|,
block|{
literal|6
block|}
block|,
block|{
literal|7
block|}
block|,
block|{
literal|8
block|}
block|,
block|{
literal|9
block|}
block|,
block|{
literal|10
block|}
block|,
block|{
literal|11
block|}
block|,
block|{
literal|12
block|}
block|,
block|{
literal|13
block|}
block|,
block|{
literal|14
block|}
block|,
block|{
literal|15
block|}
block|,
block|{
literal|16
block|}
block|,
block|{
literal|17
block|}
block|,
block|{
literal|18
block|}
block|,
block|{
literal|19
block|}
block|,
block|{
literal|20
block|}
block|,
block|{
literal|21
block|}
block|,
block|{
literal|22
block|}
block|,
block|{
literal|23
block|}
block|,
block|{
literal|24
block|}
block|,
block|{
literal|25
block|}
block|,
block|{
literal|26
block|}
block|,
block|{
literal|27
block|}
block|,
block|{
literal|28
block|}
block|,
block|{
literal|29
block|}
block|,
block|{
literal|30
block|}
block|,
block|{
literal|31
block|}
block|,
block|{
literal|32
block|}
block|,
block|{
literal|33
block|}
block|,
block|{
literal|34
block|}
block|,
block|{
literal|35
block|}
block|,
block|{
literal|36
block|}
block|,
block|{
literal|37
block|}
block|,
block|{
literal|38
block|}
block|,
block|{
literal|39
block|}
block|,         }
argument_list|)
return|;
block|}
specifier|public
name|KernelRootFuzzIT
parameter_list|(
name|int
name|seed
parameter_list|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Seed = {}"
argument_list|,
name|seed
argument_list|)
expr_stmt|;
name|random
operator|=
operator|new
name|Random
argument_list|(
name|seed
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Before
specifier|public
name|void
name|setup
parameter_list|()
block|{
name|counter
operator|=
literal|0
expr_stmt|;
name|MicroKernel
name|mk1
init|=
operator|new
name|SimpleKernelImpl
argument_list|(
literal|"mem:"
argument_list|)
decl_stmt|;
name|vf
operator|=
operator|new
name|CoreValueFactoryImpl
argument_list|(
name|mk1
argument_list|)
expr_stmt|;
name|store1
operator|=
operator|new
name|KernelNodeStore
argument_list|(
name|mk1
argument_list|,
name|vf
argument_list|)
expr_stmt|;
name|mk1
operator|.
name|commit
argument_list|(
literal|""
argument_list|,
literal|"+\"/test\":{} +\"/test/root\":{}"
argument_list|,
name|mk1
operator|.
name|getHeadRevision
argument_list|()
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|root1
operator|=
operator|new
name|KernelRoot
argument_list|(
name|store1
argument_list|,
literal|"test"
argument_list|)
expr_stmt|;
name|MicroKernel
name|mk2
init|=
operator|new
name|SimpleKernelImpl
argument_list|(
literal|"mem:"
argument_list|)
decl_stmt|;
name|store2
operator|=
operator|new
name|KernelNodeStore
argument_list|(
name|mk2
argument_list|,
name|vf
argument_list|)
expr_stmt|;
name|mk2
operator|.
name|commit
argument_list|(
literal|""
argument_list|,
literal|"+\"/test\":{} +\"/test/root\":{}"
argument_list|,
name|mk2
operator|.
name|getHeadRevision
argument_list|()
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|root2
operator|=
operator|new
name|KernelRoot
argument_list|(
name|store2
argument_list|,
literal|"test"
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
name|assertEquals
argument_list|(
name|store1
operator|.
name|getRoot
argument_list|()
argument_list|,
name|store2
operator|.
name|getRoot
argument_list|()
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
name|KernelRoot
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
name|KernelRoot
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
name|KernelRoot
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
name|removeChild
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
name|KernelRoot
name|root
parameter_list|)
block|{
name|root
operator|.
name|move
argument_list|(
name|source
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
argument_list|,
name|destination
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
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
name|CopyNode
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
name|CopyNode
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
name|KernelRoot
name|root
parameter_list|)
block|{
name|root
operator|.
name|copy
argument_list|(
name|source
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
argument_list|,
name|destination
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
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
literal|'*'
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
name|CoreValue
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
name|CoreValue
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
name|KernelRoot
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
name|KernelRoot
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
name|KernelRoot
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
comment|// Too many copy ops make the test way slow
name|op
operator|=
name|random
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
operator|==
literal|0
condition|?
name|createCopyNode
argument_list|()
else|:
literal|null
expr_stmt|;
break|break;
case|case
literal|6
case|:
name|op
operator|=
name|createAddProperty
argument_list|()
expr_stmt|;
break|break;
case|case
literal|7
case|:
name|op
operator|=
name|createSetProperty
argument_list|()
expr_stmt|;
break|break;
case|case
literal|8
case|:
name|op
operator|=
name|createRemoveProperty
argument_list|()
expr_stmt|;
break|break;
case|case
literal|9
case|:
name|op
operator|=
operator|new
name|Save
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
name|createCopyNode
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
condition|?
literal|null
else|:
operator|new
name|CopyNode
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
name|CoreValue
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
name|CoreValue
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
name|CoreValue
name|createValue
parameter_list|()
block|{
return|return
name|vf
operator|.
name|createValue
argument_list|(
literal|"V"
operator|+
name|counter
operator|++
argument_list|)
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
name|assertEquals
argument_list|(
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
name|tree1
operator|.
name|getChildrenCount
argument_list|()
argument_list|,
name|tree2
operator|.
name|getChildrenCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
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
name|assertEquals
argument_list|(
name|property1
argument_list|,
name|tree2
operator|.
name|getProperty
argument_list|(
name|property1
operator|.
name|getName
argument_list|()
argument_list|)
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

