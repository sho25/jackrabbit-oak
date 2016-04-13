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
name|upgrade
operator|.
name|util
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
name|Collections
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
name|Map
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
name|java
operator|.
name|util
operator|.
name|TreeSet
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
name|plugins
operator|.
name|segment
operator|.
name|SegmentNodeStore
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
name|segment
operator|.
name|memory
operator|.
name|MemoryStore
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
name|DefaultValidator
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
name|commit
operator|.
name|Validator
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

begin_class
specifier|public
class|class
name|NodeStateTestUtils
block|{
specifier|private
name|NodeStateTestUtils
parameter_list|()
block|{
comment|// no instances
block|}
specifier|public
specifier|static
name|NodeStore
name|createNodeStoreWithContent
parameter_list|(
name|String
modifier|...
name|paths
parameter_list|)
throws|throws
name|CommitFailedException
throws|,
name|IOException
block|{
specifier|final
name|SegmentNodeStore
name|store
init|=
name|SegmentNodeStore
operator|.
name|builder
argument_list|(
operator|new
name|MemoryStore
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
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
for|for
control|(
name|String
name|path
range|:
name|paths
control|)
block|{
name|create
argument_list|(
name|builder
argument_list|,
name|path
argument_list|)
expr_stmt|;
block|}
name|commit
argument_list|(
name|store
argument_list|,
name|builder
argument_list|)
expr_stmt|;
return|return
name|store
return|;
block|}
specifier|public
specifier|static
name|void
name|create
parameter_list|(
name|NodeBuilder
name|rootBuilder
parameter_list|,
name|String
name|path
parameter_list|,
name|PropertyState
modifier|...
name|properties
parameter_list|)
block|{
specifier|final
name|NodeBuilder
name|builder
init|=
name|createOrGetBuilder
argument_list|(
name|rootBuilder
argument_list|,
name|path
argument_list|)
decl_stmt|;
for|for
control|(
name|PropertyState
name|property
range|:
name|properties
control|)
block|{
name|builder
operator|.
name|setProperty
argument_list|(
name|property
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
specifier|static
name|void
name|commit
parameter_list|(
name|NodeStore
name|store
parameter_list|,
name|NodeBuilder
name|rootBuilder
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|store
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
block|}
specifier|public
specifier|static
name|NodeState
name|getNodeState
parameter_list|(
name|NodeState
name|state
parameter_list|,
name|String
name|path
parameter_list|)
block|{
name|NodeState
name|current
init|=
name|state
decl_stmt|;
for|for
control|(
specifier|final
name|String
name|name
range|:
name|PathUtils
operator|.
name|elements
argument_list|(
name|path
argument_list|)
control|)
block|{
name|current
operator|=
name|current
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
return|return
name|current
return|;
block|}
specifier|public
specifier|static
name|NodeBuilder
name|createOrGetBuilder
parameter_list|(
name|NodeBuilder
name|builder
parameter_list|,
name|String
name|path
parameter_list|)
block|{
name|NodeBuilder
name|current
init|=
name|builder
decl_stmt|;
for|for
control|(
specifier|final
name|String
name|name
range|:
name|PathUtils
operator|.
name|elements
argument_list|(
name|path
argument_list|)
control|)
block|{
name|current
operator|=
name|current
operator|.
name|child
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
return|return
name|current
return|;
block|}
specifier|public
specifier|static
name|void
name|assertExists
parameter_list|(
name|NodeState
name|state
parameter_list|,
name|String
name|relPath
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|relPath
operator|+
literal|" should exist"
argument_list|,
name|getNodeState
argument_list|(
name|state
argument_list|,
name|relPath
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|void
name|assertMissing
parameter_list|(
name|NodeState
name|state
parameter_list|,
name|String
name|relPath
parameter_list|)
block|{
name|assertFalse
argument_list|(
name|relPath
operator|+
literal|" should not exist"
argument_list|,
name|getNodeState
argument_list|(
name|state
argument_list|,
name|relPath
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|ExpectedDifference
name|expectDifference
parameter_list|()
block|{
return|return
operator|new
name|ExpectedDifference
argument_list|()
return|;
block|}
specifier|public
specifier|static
class|class
name|ExpectedDifference
block|{
specifier|private
name|ExpectedDifference
parameter_list|()
block|{         }
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|expected
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
specifier|public
name|void
name|verify
parameter_list|(
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|)
block|{
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|actual
init|=
name|TestValidator
operator|.
name|compare
argument_list|(
name|before
argument_list|,
name|after
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|type
range|:
name|expected
operator|.
name|keySet
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|actual
operator|.
name|containsKey
argument_list|(
name|type
argument_list|)
condition|)
block|{
name|actual
operator|.
name|put
argument_list|(
name|type
argument_list|,
name|Collections
operator|.
expr|<
name|String
operator|>
name|emptySet
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|type
argument_list|,
name|expected
operator|.
name|get
argument_list|(
name|type
argument_list|)
argument_list|,
name|actual
operator|.
name|get
argument_list|(
name|type
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|ExpectedDifference
name|propertyAdded
parameter_list|(
name|String
modifier|...
name|paths
parameter_list|)
block|{
return|return
name|expect
argument_list|(
literal|"propertyAdded"
argument_list|,
name|paths
argument_list|)
return|;
block|}
specifier|public
name|ExpectedDifference
name|propertyChanged
parameter_list|(
name|String
modifier|...
name|paths
parameter_list|)
block|{
return|return
name|expect
argument_list|(
literal|"propertyChanged"
argument_list|,
name|paths
argument_list|)
return|;
block|}
specifier|public
name|ExpectedDifference
name|propertyDeleted
parameter_list|(
name|String
modifier|...
name|paths
parameter_list|)
block|{
return|return
name|expect
argument_list|(
literal|"propertyDeleted"
argument_list|,
name|paths
argument_list|)
return|;
block|}
specifier|public
name|ExpectedDifference
name|childNodeAdded
parameter_list|(
name|String
modifier|...
name|paths
parameter_list|)
block|{
return|return
name|expect
argument_list|(
literal|"childNodeAdded"
argument_list|,
name|paths
argument_list|)
return|;
block|}
specifier|public
name|ExpectedDifference
name|childNodeChanged
parameter_list|(
name|String
modifier|...
name|paths
parameter_list|)
block|{
return|return
name|expect
argument_list|(
literal|"childNodeChanged"
argument_list|,
name|paths
argument_list|)
return|;
block|}
specifier|public
name|ExpectedDifference
name|childNodeDeleted
parameter_list|(
name|String
modifier|...
name|paths
parameter_list|)
block|{
return|return
name|expect
argument_list|(
literal|"childNodeDeleted"
argument_list|,
name|paths
argument_list|)
return|;
block|}
specifier|public
name|ExpectedDifference
name|strict
parameter_list|()
block|{
return|return
name|this
operator|.
name|propertyAdded
argument_list|()
operator|.
name|propertyChanged
argument_list|()
operator|.
name|propertyDeleted
argument_list|()
operator|.
name|childNodeAdded
argument_list|()
operator|.
name|childNodeChanged
argument_list|()
operator|.
name|childNodeDeleted
argument_list|()
return|;
block|}
specifier|private
name|ExpectedDifference
name|expect
parameter_list|(
name|String
name|type
parameter_list|,
name|String
modifier|...
name|paths
parameter_list|)
block|{
if|if
condition|(
operator|!
name|expected
operator|.
name|containsKey
argument_list|(
name|type
argument_list|)
condition|)
block|{
name|expected
operator|.
name|put
argument_list|(
name|type
argument_list|,
operator|new
name|TreeSet
argument_list|<
name|String
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|Collections
operator|.
name|addAll
argument_list|(
name|expected
operator|.
name|get
argument_list|(
name|type
argument_list|)
argument_list|,
name|paths
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
block|}
specifier|private
specifier|static
class|class
name|TestValidator
extends|extends
name|DefaultValidator
block|{
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|actual
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
name|String
name|path
init|=
literal|"/"
decl_stmt|;
specifier|public
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|compare
parameter_list|(
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|)
block|{
specifier|final
name|TestValidator
name|validator
init|=
operator|new
name|TestValidator
argument_list|()
decl_stmt|;
name|EditorDiff
operator|.
name|process
argument_list|(
name|validator
argument_list|,
name|before
argument_list|,
name|after
argument_list|)
expr_stmt|;
return|return
name|validator
operator|.
name|actual
return|;
block|}
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
name|path
operator|=
name|PathUtils
operator|.
name|getParentPath
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|propertyAdded
parameter_list|(
name|PropertyState
name|after
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|record
argument_list|(
literal|"propertyAdded"
argument_list|,
name|PathUtils
operator|.
name|concat
argument_list|(
name|path
argument_list|,
name|after
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|propertyChanged
parameter_list|(
name|PropertyState
name|before
parameter_list|,
name|PropertyState
name|after
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|record
argument_list|(
literal|"propertyChanged"
argument_list|,
name|PathUtils
operator|.
name|concat
argument_list|(
name|path
argument_list|,
name|after
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|propertyDeleted
parameter_list|(
name|PropertyState
name|before
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|record
argument_list|(
literal|"propertyDeleted"
argument_list|,
name|PathUtils
operator|.
name|concat
argument_list|(
name|path
argument_list|,
name|before
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Validator
name|childNodeAdded
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|after
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|path
operator|=
name|PathUtils
operator|.
name|concat
argument_list|(
name|path
argument_list|,
name|name
argument_list|)
expr_stmt|;
name|record
argument_list|(
literal|"childNodeAdded"
argument_list|,
name|path
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
specifier|public
name|Validator
name|childNodeChanged
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|)
throws|throws
name|CommitFailedException
block|{
comment|// make sure not to record false positives (inefficient for large trees)
if|if
condition|(
operator|!
name|before
operator|.
name|equals
argument_list|(
name|after
argument_list|)
condition|)
block|{
name|path
operator|=
name|PathUtils
operator|.
name|concat
argument_list|(
name|path
argument_list|,
name|name
argument_list|)
expr_stmt|;
name|record
argument_list|(
literal|"childNodeChanged"
argument_list|,
name|path
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|Validator
name|childNodeDeleted
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|before
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|path
operator|=
name|PathUtils
operator|.
name|concat
argument_list|(
name|path
argument_list|,
name|name
argument_list|)
expr_stmt|;
name|record
argument_list|(
literal|"childNodeDeleted"
argument_list|,
name|path
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|private
name|void
name|record
parameter_list|(
name|String
name|type
parameter_list|,
name|String
name|path
parameter_list|)
block|{
if|if
condition|(
operator|!
name|actual
operator|.
name|containsKey
argument_list|(
name|type
argument_list|)
condition|)
block|{
name|actual
operator|.
name|put
argument_list|(
name|type
argument_list|,
operator|new
name|TreeSet
argument_list|<
name|String
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|actual
operator|.
name|get
argument_list|(
name|type
argument_list|)
operator|.
name|add
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

