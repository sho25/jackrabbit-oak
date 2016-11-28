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
name|observation
operator|.
name|filter
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
name|HashSet
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
name|observation
operator|.
name|ChangeSet
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
name|observation
operator|.
name|ChangeSetBuilder
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

begin_class
specifier|public
class|class
name|ChangeSetFilterImplTest
block|{
comment|/** shortcut for creating a set of strings */
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|s
parameter_list|(
name|String
modifier|...
name|entries
parameter_list|)
block|{
return|return
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|entries
argument_list|)
argument_list|)
return|;
block|}
specifier|private
name|ChangeSet
name|newChangeSet
parameter_list|(
name|int
name|maxPathDepth
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|parentPaths
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|parentNodeNames
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|parentNodeTypes
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|propertyNames
parameter_list|)
block|{
return|return
name|newChangeSet
argument_list|(
name|maxPathDepth
argument_list|,
name|parentPaths
argument_list|,
name|parentNodeNames
argument_list|,
name|parentNodeTypes
argument_list|,
name|propertyNames
argument_list|,
name|s
argument_list|()
argument_list|)
return|;
block|}
specifier|private
name|ChangeSet
name|newChangeSet
parameter_list|(
name|int
name|maxPathDepth
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|parentPaths
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|parentNodeNames
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|parentNodeTypes
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|propertyNames
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|allNodeTypes
parameter_list|)
block|{
name|ChangeSetBuilder
name|changeSetBuilder
init|=
operator|new
name|ChangeSetBuilder
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
name|maxPathDepth
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|path
range|:
name|parentPaths
control|)
block|{
name|changeSetBuilder
operator|.
name|addParentPath
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|nodeName
range|:
name|parentNodeNames
control|)
block|{
name|changeSetBuilder
operator|.
name|addParentNodeName
argument_list|(
name|nodeName
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|parentNodeType
range|:
name|parentNodeTypes
control|)
block|{
name|changeSetBuilder
operator|.
name|addParentNodeType
argument_list|(
name|parentNodeType
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|propertyName
range|:
name|propertyNames
control|)
block|{
name|changeSetBuilder
operator|.
name|addPropertyName
argument_list|(
name|propertyName
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|nodeType
range|:
name|allNodeTypes
control|)
block|{
name|changeSetBuilder
operator|.
name|addNodeType
argument_list|(
name|nodeType
argument_list|)
expr_stmt|;
block|}
return|return
name|changeSetBuilder
operator|.
name|build
argument_list|()
return|;
block|}
specifier|private
name|ChangeSetBuilder
name|newBuilder
parameter_list|(
name|int
name|maxItems
parameter_list|,
name|int
name|maxPathDepth
parameter_list|)
block|{
return|return
operator|new
name|ChangeSetBuilder
argument_list|(
name|maxItems
argument_list|,
name|maxPathDepth
argument_list|)
return|;
block|}
specifier|private
name|ChangeSetBuilder
name|overflowAllNodeTypes
parameter_list|(
name|ChangeSetBuilder
name|builder
parameter_list|)
block|{
name|int
name|i
init|=
literal|0
decl_stmt|;
while|while
condition|(
operator|!
name|builder
operator|.
name|isAllNodeTypeOverflown
argument_list|()
condition|)
block|{
name|builder
operator|.
name|addNodeType
argument_list|(
literal|"foo"
operator|+
name|i
operator|++
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
return|;
block|}
specifier|private
name|ChangeSetBuilder
name|overflowParentNodeTypes
parameter_list|(
name|ChangeSetBuilder
name|builder
parameter_list|)
block|{
name|int
name|i
init|=
literal|0
decl_stmt|;
while|while
condition|(
operator|!
name|builder
operator|.
name|isParentNodeTypeOverflown
argument_list|()
condition|)
block|{
name|builder
operator|.
name|addParentNodeType
argument_list|(
literal|"foo"
operator|+
name|i
operator|++
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
return|;
block|}
specifier|private
name|ChangeSetBuilder
name|overflowParentNodeNames
parameter_list|(
name|ChangeSetBuilder
name|builder
parameter_list|)
block|{
name|int
name|i
init|=
literal|0
decl_stmt|;
while|while
condition|(
operator|!
name|builder
operator|.
name|isParentNodeNameOverflown
argument_list|()
condition|)
block|{
name|builder
operator|.
name|addParentNodeName
argument_list|(
literal|"foo"
operator|+
name|i
operator|++
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
return|;
block|}
specifier|private
name|ChangeSetBuilder
name|overflowParentPaths
parameter_list|(
name|ChangeSetBuilder
name|builder
parameter_list|)
block|{
name|int
name|i
init|=
literal|0
decl_stmt|;
while|while
condition|(
operator|!
name|builder
operator|.
name|isParentPathOverflown
argument_list|()
condition|)
block|{
name|builder
operator|.
name|addParentPath
argument_list|(
literal|"foo"
operator|+
name|i
operator|++
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
return|;
block|}
specifier|private
name|ChangeSetBuilder
name|overflowPropertyNames
parameter_list|(
name|ChangeSetBuilder
name|builder
parameter_list|)
block|{
name|int
name|i
init|=
literal|0
decl_stmt|;
while|while
condition|(
operator|!
name|builder
operator|.
name|isPropertyNameOverflown
argument_list|()
condition|)
block|{
name|builder
operator|.
name|addPropertyName
argument_list|(
literal|"foo"
operator|+
name|i
operator|++
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testIsDeepFalse
parameter_list|()
throws|throws
name|Exception
block|{
name|ChangeSetFilterImpl
name|prefilter
init|=
operator|new
name|ChangeSetFilterImpl
argument_list|(
name|s
argument_list|(
literal|"/"
argument_list|)
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|,
name|s
argument_list|(
literal|"/excluded"
argument_list|)
argument_list|,
name|s
argument_list|()
argument_list|,
name|s
argument_list|()
argument_list|,
name|s
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|prefilter
operator|.
name|excludes
argument_list|(
name|newChangeSet
argument_list|(
literal|5
argument_list|,
name|s
argument_list|(
literal|"/child1"
argument_list|,
literal|"/child2"
argument_list|)
argument_list|,
name|s
argument_list|(
literal|"child1"
argument_list|,
literal|"child2"
argument_list|)
argument_list|,
name|s
argument_list|()
argument_list|,
name|s
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|prefilter
operator|.
name|excludes
argument_list|(
name|newChangeSet
argument_list|(
literal|5
argument_list|,
name|s
argument_list|(
literal|"/"
argument_list|,
literal|"/child2"
argument_list|)
argument_list|,
name|s
argument_list|(
literal|"child2"
argument_list|)
argument_list|,
name|s
argument_list|()
argument_list|,
name|s
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testParentPathsIncludeExclude
parameter_list|()
throws|throws
name|Exception
block|{
name|ChangeSetFilterImpl
name|prefilter
init|=
operator|new
name|ChangeSetFilterImpl
argument_list|(
name|s
argument_list|(
literal|"/"
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|,
name|s
argument_list|(
literal|"/excluded"
argument_list|)
argument_list|,
name|s
argument_list|()
argument_list|,
name|s
argument_list|()
argument_list|,
name|s
argument_list|()
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|prefilter
operator|.
name|excludes
argument_list|(
name|newChangeSet
argument_list|(
literal|5
argument_list|,
name|s
argument_list|(
literal|"/a"
argument_list|,
literal|"/b"
argument_list|)
argument_list|,
name|s
argument_list|(
literal|"a"
argument_list|,
literal|"b"
argument_list|)
argument_list|,
name|s
argument_list|()
argument_list|,
name|s
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|prefilter
operator|.
name|excludes
argument_list|(
name|newChangeSet
argument_list|(
literal|5
argument_list|,
name|s
argument_list|(
literal|"/excluded/foo"
argument_list|,
literal|"/excluded/bar"
argument_list|)
argument_list|,
name|s
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
argument_list|,
name|s
argument_list|()
argument_list|,
name|s
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|prefilter
operator|=
operator|new
name|ChangeSetFilterImpl
argument_list|(
name|s
argument_list|(
literal|"/included"
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|,
name|s
argument_list|(
literal|"/excluded"
argument_list|)
argument_list|,
name|s
argument_list|()
argument_list|,
name|s
argument_list|()
argument_list|,
name|s
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|prefilter
operator|.
name|excludes
argument_list|(
name|newChangeSet
argument_list|(
literal|5
argument_list|,
name|s
argument_list|(
literal|"/a"
argument_list|,
literal|"/b"
argument_list|)
argument_list|,
name|s
argument_list|()
argument_list|,
name|s
argument_list|()
argument_list|,
name|s
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|prefilter
operator|.
name|excludes
argument_list|(
name|newChangeSet
argument_list|(
literal|5
argument_list|,
name|s
argument_list|(
literal|"/included/a"
argument_list|,
literal|"/included/b"
argument_list|)
argument_list|,
name|s
argument_list|()
argument_list|,
name|s
argument_list|()
argument_list|,
name|s
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|prefilter
operator|.
name|excludes
argument_list|(
name|newChangeSet
argument_list|(
literal|5
argument_list|,
name|s
argument_list|(
literal|"/excluded/foo"
argument_list|,
literal|"/excluded/bar"
argument_list|)
argument_list|,
name|s
argument_list|()
argument_list|,
name|s
argument_list|()
argument_list|,
name|s
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|prefilter
operator|=
operator|new
name|ChangeSetFilterImpl
argument_list|(
name|s
argument_list|(
literal|"/foo/**/included/**"
argument_list|)
argument_list|,
literal|true
comment|/*ignored for globs */
argument_list|,
literal|null
argument_list|,
name|s
argument_list|(
literal|"/excluded"
argument_list|)
argument_list|,
name|s
argument_list|()
argument_list|,
name|s
argument_list|()
argument_list|,
name|s
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|prefilter
operator|.
name|excludes
argument_list|(
name|newChangeSet
argument_list|(
literal|5
argument_list|,
name|s
argument_list|(
literal|"/a"
argument_list|,
literal|"/b"
argument_list|)
argument_list|,
name|s
argument_list|()
argument_list|,
name|s
argument_list|()
argument_list|,
name|s
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|prefilter
operator|.
name|excludes
argument_list|(
name|newChangeSet
argument_list|(
literal|5
argument_list|,
name|s
argument_list|(
literal|"/included/a"
argument_list|,
literal|"/included/b"
argument_list|)
argument_list|,
name|s
argument_list|()
argument_list|,
name|s
argument_list|()
argument_list|,
name|s
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|prefilter
operator|.
name|excludes
argument_list|(
name|newChangeSet
argument_list|(
literal|5
argument_list|,
name|s
argument_list|(
literal|"/foo/included/a"
argument_list|)
argument_list|,
name|s
argument_list|()
argument_list|,
name|s
argument_list|()
argument_list|,
name|s
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|prefilter
operator|.
name|excludes
argument_list|(
name|newChangeSet
argument_list|(
literal|5
argument_list|,
name|s
argument_list|(
literal|"/included/b"
argument_list|)
argument_list|,
name|s
argument_list|()
argument_list|,
name|s
argument_list|()
argument_list|,
name|s
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|prefilter
operator|.
name|excludes
argument_list|(
name|newChangeSet
argument_list|(
literal|5
argument_list|,
name|s
argument_list|(
literal|"/foo/bar/included/a"
argument_list|,
literal|"/included/b"
argument_list|)
argument_list|,
name|s
argument_list|()
argument_list|,
name|s
argument_list|()
argument_list|,
name|s
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|prefilter
operator|.
name|excludes
argument_list|(
name|newChangeSet
argument_list|(
literal|5
argument_list|,
name|s
argument_list|(
literal|"/excluded/foo"
argument_list|,
literal|"/excluded/bar"
argument_list|)
argument_list|,
name|s
argument_list|()
argument_list|,
name|s
argument_list|()
argument_list|,
name|s
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|prefilter
operator|=
operator|new
name|ChangeSetFilterImpl
argument_list|(
name|s
argument_list|(
literal|"/main/**/included"
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|,
name|s
argument_list|(
literal|"/main/excluded"
argument_list|)
argument_list|,
name|s
argument_list|()
argument_list|,
name|s
argument_list|()
argument_list|,
name|s
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|prefilter
operator|.
name|excludes
argument_list|(
name|newChangeSet
argument_list|(
literal|5
argument_list|,
name|s
argument_list|(
literal|"/main"
argument_list|,
literal|"/main/foo"
argument_list|)
argument_list|,
name|s
argument_list|()
argument_list|,
name|s
argument_list|()
argument_list|,
name|s
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|prefilter
operator|.
name|excludes
argument_list|(
name|newChangeSet
argument_list|(
literal|5
argument_list|,
name|s
argument_list|(
literal|"/main/included"
argument_list|,
literal|"/main/excluded"
argument_list|)
argument_list|,
name|s
argument_list|()
argument_list|,
name|s
argument_list|()
argument_list|,
name|s
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|prefilter
operator|.
name|excludes
argument_list|(
name|newChangeSet
argument_list|(
literal|5
argument_list|,
name|s
argument_list|(
literal|"/main/excluded/included"
argument_list|,
literal|"/main/excluded"
argument_list|)
argument_list|,
name|s
argument_list|()
argument_list|,
name|s
argument_list|()
argument_list|,
name|s
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|prefilter
operator|=
operator|new
name|ChangeSetFilterImpl
argument_list|(
name|s
argument_list|(
literal|"/main/included/**"
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|,
name|s
argument_list|(
literal|"/main/excluded"
argument_list|)
argument_list|,
name|s
argument_list|()
argument_list|,
name|s
argument_list|()
argument_list|,
name|s
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|prefilter
operator|.
name|excludes
argument_list|(
name|newChangeSet
argument_list|(
literal|5
argument_list|,
name|s
argument_list|(
literal|"/main"
argument_list|,
literal|"/main/foo"
argument_list|)
argument_list|,
name|s
argument_list|()
argument_list|,
name|s
argument_list|()
argument_list|,
name|s
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|prefilter
operator|.
name|excludes
argument_list|(
name|newChangeSet
argument_list|(
literal|5
argument_list|,
name|s
argument_list|(
literal|"/main/excluded"
argument_list|)
argument_list|,
name|s
argument_list|()
argument_list|,
name|s
argument_list|()
argument_list|,
name|s
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|prefilter
operator|.
name|excludes
argument_list|(
name|newChangeSet
argument_list|(
literal|5
argument_list|,
name|s
argument_list|(
literal|"/main/included"
argument_list|)
argument_list|,
name|s
argument_list|()
argument_list|,
name|s
argument_list|()
argument_list|,
name|s
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|prefilter
operator|.
name|excludes
argument_list|(
name|newChangeSet
argument_list|(
literal|5
argument_list|,
name|s
argument_list|(
literal|"/main/excluded/included"
argument_list|,
literal|"/main/excluded"
argument_list|)
argument_list|,
name|s
argument_list|()
argument_list|,
name|s
argument_list|()
argument_list|,
name|s
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|prefilter
operator|=
operator|new
name|ChangeSetFilterImpl
argument_list|(
name|s
argument_list|(
literal|"/main/inc-*/**"
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|,
name|s
argument_list|(
literal|"/main/excluded"
argument_list|)
argument_list|,
name|s
argument_list|()
argument_list|,
name|s
argument_list|()
argument_list|,
name|s
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|prefilter
operator|.
name|excludes
argument_list|(
name|newChangeSet
argument_list|(
literal|5
argument_list|,
name|s
argument_list|(
literal|"/main"
argument_list|,
literal|"/main/foo"
argument_list|)
argument_list|,
name|s
argument_list|()
argument_list|,
name|s
argument_list|()
argument_list|,
name|s
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|prefilter
operator|.
name|excludes
argument_list|(
name|newChangeSet
argument_list|(
literal|5
argument_list|,
name|s
argument_list|(
literal|"/main/excluded"
argument_list|)
argument_list|,
name|s
argument_list|()
argument_list|,
name|s
argument_list|()
argument_list|,
name|s
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|prefilter
operator|.
name|excludes
argument_list|(
name|newChangeSet
argument_list|(
literal|5
argument_list|,
name|s
argument_list|(
literal|"/main/inc-luded"
argument_list|)
argument_list|,
name|s
argument_list|()
argument_list|,
name|s
argument_list|()
argument_list|,
name|s
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|prefilter
operator|.
name|excludes
argument_list|(
name|newChangeSet
argument_list|(
literal|5
argument_list|,
name|s
argument_list|(
literal|"/main/excluded/included"
argument_list|,
literal|"/main/excluded"
argument_list|)
argument_list|,
name|s
argument_list|()
argument_list|,
name|s
argument_list|()
argument_list|,
name|s
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testParentNodeNames
parameter_list|()
throws|throws
name|Exception
block|{
name|ChangeSetFilterImpl
name|prefilter
init|=
operator|new
name|ChangeSetFilterImpl
argument_list|(
name|s
argument_list|(
literal|"/"
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|,
name|s
argument_list|()
argument_list|,
name|s
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
argument_list|,
name|s
argument_list|()
argument_list|,
name|s
argument_list|()
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|prefilter
operator|.
name|excludes
argument_list|(
name|newChangeSet
argument_list|(
literal|5
argument_list|,
name|s
argument_list|(
literal|"/a/foo"
argument_list|,
literal|"/b"
argument_list|)
argument_list|,
name|s
argument_list|(
literal|"foo"
argument_list|,
literal|"b"
argument_list|)
argument_list|,
name|s
argument_list|()
argument_list|,
name|s
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|prefilter
operator|.
name|excludes
argument_list|(
name|newChangeSet
argument_list|(
literal|5
argument_list|,
name|s
argument_list|(
literal|"/a/zoo"
argument_list|,
literal|"/b"
argument_list|)
argument_list|,
name|s
argument_list|(
literal|"zoo"
argument_list|,
literal|"b"
argument_list|)
argument_list|,
name|s
argument_list|()
argument_list|,
name|s
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|prefilter
operator|.
name|excludes
argument_list|(
name|newChangeSet
argument_list|(
literal|5
argument_list|,
name|s
argument_list|(
literal|"/a/zoo"
argument_list|,
literal|"/bar"
argument_list|)
argument_list|,
name|s
argument_list|(
literal|"zoo"
argument_list|,
literal|"bar"
argument_list|)
argument_list|,
name|s
argument_list|()
argument_list|,
name|s
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testParentNodeTypes
parameter_list|()
throws|throws
name|Exception
block|{
name|ChangeSetFilterImpl
name|prefilter
init|=
operator|new
name|ChangeSetFilterImpl
argument_list|(
name|s
argument_list|(
literal|"/"
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|,
name|s
argument_list|()
argument_list|,
name|s
argument_list|()
argument_list|,
name|s
argument_list|(
literal|"nt:folder"
argument_list|)
argument_list|,
name|s
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|prefilter
operator|.
name|excludes
argument_list|(
name|newChangeSet
argument_list|(
literal|5
argument_list|,
name|s
argument_list|(
literal|"/a"
argument_list|)
argument_list|,
name|s
argument_list|(
literal|"a"
argument_list|)
argument_list|,
name|s
argument_list|(
literal|"nt:unstructured"
argument_list|)
argument_list|,
name|s
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|prefilter
operator|.
name|excludes
argument_list|(
name|newChangeSet
argument_list|(
literal|5
argument_list|,
name|s
argument_list|(
literal|"/a"
argument_list|)
argument_list|,
name|s
argument_list|(
literal|"a"
argument_list|)
argument_list|,
name|s
argument_list|(
literal|"nt:folder"
argument_list|)
argument_list|,
name|s
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPropertyNames
parameter_list|()
throws|throws
name|Exception
block|{
name|ChangeSetFilterImpl
name|prefilter
init|=
operator|new
name|ChangeSetFilterImpl
argument_list|(
name|s
argument_list|(
literal|"/"
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|,
name|s
argument_list|()
argument_list|,
name|s
argument_list|()
argument_list|,
name|s
argument_list|()
argument_list|,
name|s
argument_list|(
literal|"jcr:data"
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|prefilter
operator|.
name|excludes
argument_list|(
name|newChangeSet
argument_list|(
literal|5
argument_list|,
name|s
argument_list|(
literal|"/a"
argument_list|)
argument_list|,
name|s
argument_list|(
literal|"a"
argument_list|)
argument_list|,
name|s
argument_list|()
argument_list|,
name|s
argument_list|(
literal|"myProperty"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|prefilter
operator|.
name|excludes
argument_list|(
name|newChangeSet
argument_list|(
literal|5
argument_list|,
name|s
argument_list|(
literal|"/a"
argument_list|)
argument_list|,
name|s
argument_list|(
literal|"a"
argument_list|)
argument_list|,
name|s
argument_list|()
argument_list|,
name|s
argument_list|(
literal|"jcr:data"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testOverflowing
parameter_list|()
throws|throws
name|Exception
block|{
name|ChangeSetBuilder
name|builder
init|=
name|newBuilder
argument_list|(
literal|5
argument_list|,
literal|5
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|overflowAllNodeTypes
argument_list|(
name|builder
argument_list|)
operator|.
name|isAllNodeTypeOverflown
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|overflowParentNodeTypes
argument_list|(
name|builder
argument_list|)
operator|.
name|isParentNodeTypeOverflown
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|overflowParentNodeNames
argument_list|(
name|builder
argument_list|)
operator|.
name|isParentNodeNameOverflown
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|overflowParentPaths
argument_list|(
name|builder
argument_list|)
operator|.
name|isParentPathOverflown
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|overflowPropertyNames
argument_list|(
name|builder
argument_list|)
operator|.
name|isPropertyNameOverflown
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|ChangeSetBuilder
name|sampleBuilder
parameter_list|()
block|{
name|ChangeSetBuilder
name|builder
init|=
name|newBuilder
argument_list|(
literal|5
argument_list|,
literal|5
argument_list|)
decl_stmt|;
name|builder
operator|.
name|addNodeType
argument_list|(
literal|"nt:file"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|addParentNodeType
argument_list|(
literal|"nt:file"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|addParentPath
argument_list|(
literal|"/bar"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|addParentNodeName
argument_list|(
literal|"bar"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|addPropertyName
argument_list|(
literal|"a"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|addPropertyName
argument_list|(
literal|"b"
argument_list|)
expr_stmt|;
return|return
name|builder
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testIncludeOnAllNodeTypeOverflow
parameter_list|()
throws|throws
name|Exception
block|{
name|ChangeSetBuilder
name|builder
init|=
name|sampleBuilder
argument_list|()
decl_stmt|;
name|ChangeSetFilterImpl
name|prefilter
init|=
operator|new
name|ChangeSetFilterImpl
argument_list|(
name|s
argument_list|(
literal|"/"
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|,
name|s
argument_list|(
literal|"/excluded"
argument_list|)
argument_list|,
name|s
argument_list|(
literal|"foo"
argument_list|,
literal|"bars"
argument_list|)
argument_list|,
name|s
argument_list|(
literal|"nt:file"
argument_list|)
argument_list|,
name|s
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|prefilter
operator|.
name|excludes
argument_list|(
name|builder
operator|.
name|build
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|overflowAllNodeTypes
argument_list|(
name|builder
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|prefilter
operator|.
name|excludes
argument_list|(
name|builder
operator|.
name|build
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testIncludeOnParentNodeNameOverflow
parameter_list|()
throws|throws
name|Exception
block|{
name|ChangeSetBuilder
name|builder
init|=
name|sampleBuilder
argument_list|()
decl_stmt|;
name|ChangeSetFilterImpl
name|prefilter
init|=
operator|new
name|ChangeSetFilterImpl
argument_list|(
name|s
argument_list|(
literal|"/"
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|,
name|s
argument_list|(
literal|"/excluded"
argument_list|)
argument_list|,
name|s
argument_list|(
literal|"foo"
argument_list|,
literal|"bars"
argument_list|)
argument_list|,
name|s
argument_list|(
literal|"nt:file"
argument_list|)
argument_list|,
name|s
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|prefilter
operator|.
name|excludes
argument_list|(
name|builder
operator|.
name|build
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|overflowParentNodeNames
argument_list|(
name|builder
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|prefilter
operator|.
name|excludes
argument_list|(
name|builder
operator|.
name|build
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testIncludeOnPropertyNamesOverflow
parameter_list|()
throws|throws
name|Exception
block|{
name|ChangeSetBuilder
name|builder
init|=
name|sampleBuilder
argument_list|()
decl_stmt|;
name|ChangeSetFilterImpl
name|prefilter
init|=
operator|new
name|ChangeSetFilterImpl
argument_list|(
name|s
argument_list|(
literal|"/"
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|,
name|s
argument_list|(
literal|"/excluded"
argument_list|)
argument_list|,
name|s
argument_list|(
literal|"foo"
argument_list|,
literal|"bars"
argument_list|)
argument_list|,
name|s
argument_list|(
literal|"nt:file"
argument_list|)
argument_list|,
name|s
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|prefilter
operator|.
name|excludes
argument_list|(
name|builder
operator|.
name|build
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|overflowPropertyNames
argument_list|(
name|builder
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|prefilter
operator|.
name|excludes
argument_list|(
name|builder
operator|.
name|build
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testIncludeOnParentNodeTypeOverflow
parameter_list|()
throws|throws
name|Exception
block|{
name|ChangeSetBuilder
name|builder
init|=
name|sampleBuilder
argument_list|()
decl_stmt|;
name|ChangeSetFilterImpl
name|prefilter
init|=
operator|new
name|ChangeSetFilterImpl
argument_list|(
name|s
argument_list|(
literal|"/"
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|,
name|s
argument_list|(
literal|"/excluded"
argument_list|)
argument_list|,
name|s
argument_list|(
literal|"foo"
argument_list|,
literal|"bars"
argument_list|)
argument_list|,
name|s
argument_list|(
literal|"nt:file"
argument_list|)
argument_list|,
name|s
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|prefilter
operator|.
name|excludes
argument_list|(
name|builder
operator|.
name|build
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|overflowParentNodeTypes
argument_list|(
name|builder
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|prefilter
operator|.
name|excludes
argument_list|(
name|builder
operator|.
name|build
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testIncludeOnParentPathsOverflow
parameter_list|()
throws|throws
name|Exception
block|{
name|ChangeSetBuilder
name|builder
init|=
name|sampleBuilder
argument_list|()
decl_stmt|;
name|ChangeSetFilterImpl
name|prefilter
init|=
operator|new
name|ChangeSetFilterImpl
argument_list|(
name|s
argument_list|(
literal|"/"
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|,
name|s
argument_list|(
literal|"/excluded"
argument_list|)
argument_list|,
name|s
argument_list|(
literal|"foo"
argument_list|,
literal|"bars"
argument_list|)
argument_list|,
name|s
argument_list|(
literal|"nt:file"
argument_list|)
argument_list|,
name|s
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|prefilter
operator|.
name|excludes
argument_list|(
name|builder
operator|.
name|build
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|overflowParentPaths
argument_list|(
name|builder
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|prefilter
operator|.
name|excludes
argument_list|(
name|builder
operator|.
name|build
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUnpreciseInclude
parameter_list|()
throws|throws
name|Exception
block|{
name|ChangeSetBuilder
name|builder
init|=
name|newBuilder
argument_list|(
literal|5
argument_list|,
literal|5
argument_list|)
decl_stmt|;
name|builder
operator|.
name|addNodeType
argument_list|(
literal|"nt:file"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|addParentNodeType
argument_list|(
literal|"nt:file"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|addParentPath
argument_list|(
literal|"/a/b/c/e"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|addParentNodeName
argument_list|(
literal|"d"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|addPropertyName
argument_list|(
literal|"e"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|addPropertyName
argument_list|(
literal|"f"
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|largeExcludeSet
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|a
init|=
literal|0
init|;
name|a
operator|<
literal|3
condition|;
name|a
operator|++
control|)
block|{
for|for
control|(
name|int
name|b
init|=
literal|0
init|;
name|b
operator|<
literal|3
condition|;
name|b
operator|++
control|)
block|{
for|for
control|(
name|int
name|c
init|=
literal|0
init|;
name|c
operator|<
literal|10
condition|;
name|c
operator|++
control|)
block|{
name|String
name|s
init|=
literal|"/a"
decl_stmt|;
if|if
condition|(
name|a
operator|>
literal|0
condition|)
block|{
name|s
operator|+=
name|a
expr_stmt|;
block|}
name|s
operator|+=
literal|"/b"
expr_stmt|;
if|if
condition|(
name|b
operator|>
literal|0
condition|)
block|{
name|s
operator|+=
name|b
expr_stmt|;
block|}
name|s
operator|+=
literal|"/c"
expr_stmt|;
if|if
condition|(
name|c
operator|>
literal|0
condition|)
block|{
name|s
operator|+=
name|c
expr_stmt|;
block|}
name|s
operator|+=
literal|"/d"
expr_stmt|;
name|largeExcludeSet
operator|.
name|add
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|ChangeSetFilterImpl
name|prefilter
init|=
operator|new
name|ChangeSetFilterImpl
argument_list|(
name|s
argument_list|(
literal|"/"
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|,
name|largeExcludeSet
argument_list|,
name|s
argument_list|(
literal|"foo"
argument_list|,
literal|"bars"
argument_list|)
argument_list|,
name|s
argument_list|(
literal|"nt:file"
argument_list|)
argument_list|,
name|s
argument_list|()
argument_list|,
literal|999
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|prefilter
operator|.
name|excludes
argument_list|(
name|builder
operator|.
name|build
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|prefilter
operator|=
operator|new
name|ChangeSetFilterImpl
argument_list|(
name|s
argument_list|(
literal|"/"
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|,
name|largeExcludeSet
argument_list|,
name|s
argument_list|(
literal|"foo"
argument_list|,
literal|"bars"
argument_list|)
argument_list|,
name|s
argument_list|(
literal|"nt:file"
argument_list|)
argument_list|,
name|s
argument_list|()
argument_list|,
literal|15
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|prefilter
operator|.
name|excludes
argument_list|(
name|builder
operator|.
name|build
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|prefilter
operator|=
operator|new
name|ChangeSetFilterImpl
argument_list|(
name|s
argument_list|(
literal|"/"
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|,
name|largeExcludeSet
argument_list|,
name|s
argument_list|(
literal|"foo"
argument_list|,
literal|"bars"
argument_list|)
argument_list|,
name|s
argument_list|(
literal|"nt:file"
argument_list|)
argument_list|,
name|s
argument_list|()
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|prefilter
operator|.
name|excludes
argument_list|(
name|builder
operator|.
name|build
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

