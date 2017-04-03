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
name|benchmark
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Item
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Node
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Property
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|RepositoryException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Session
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|util
operator|.
name|TraversingItemVisitor
import|;
end_import

begin_comment
comment|/**  * Concurrently reads random items from the deep tree and traverses the the  * subtree until {@code MAX_LEVEL} is reached, which is currently set to 10.  */
end_comment

begin_class
specifier|public
class|class
name|ConcurrentTraversalTest
extends|extends
name|ManyUserReadTest
block|{
comment|/* number of levels to traverse */
specifier|private
specifier|static
specifier|final
name|int
name|MAX_LEVEL
init|=
literal|10
decl_stmt|;
specifier|protected
name|ConcurrentTraversalTest
parameter_list|(
name|boolean
name|runAsAdmin
parameter_list|,
name|int
name|itemsToRead
parameter_list|,
name|boolean
name|doReport
parameter_list|,
name|boolean
name|randomUser
parameter_list|)
block|{
name|super
argument_list|(
name|runAsAdmin
argument_list|,
name|itemsToRead
argument_list|,
name|doReport
argument_list|,
name|randomUser
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|runTest
parameter_list|()
throws|throws
name|Exception
block|{
name|traverse
argument_list|(
name|testSession
argument_list|,
name|itemsToRead
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|traverse
parameter_list|(
name|Session
name|testSession
parameter_list|,
name|int
name|cnt
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|boolean
name|logout
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|testSession
operator|==
literal|null
condition|)
block|{
name|testSession
operator|=
name|getTestSession
argument_list|()
expr_stmt|;
name|logout
operator|=
literal|true
expr_stmt|;
block|}
try|try
block|{
name|int
name|nodeCnt
init|=
literal|0
decl_stmt|;
name|int
name|propertyCnt
init|=
literal|0
decl_stmt|;
name|int
name|noAccess
init|=
literal|0
decl_stmt|;
name|int
name|size
init|=
name|allPaths
operator|.
name|size
argument_list|()
decl_stmt|;
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
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
name|cnt
condition|;
name|i
operator|++
control|)
block|{
name|double
name|rand
init|=
name|size
operator|*
name|Math
operator|.
name|random
argument_list|()
decl_stmt|;
name|int
name|index
init|=
operator|(
name|int
operator|)
name|Math
operator|.
name|floor
argument_list|(
name|rand
argument_list|)
decl_stmt|;
name|String
name|path
init|=
name|allPaths
operator|.
name|get
argument_list|(
name|index
argument_list|)
decl_stmt|;
if|if
condition|(
name|testSession
operator|.
name|itemExists
argument_list|(
name|path
argument_list|)
condition|)
block|{
name|Item
name|item
init|=
name|testSession
operator|.
name|getItem
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|Visitor
name|visitor
init|=
operator|new
name|Visitor
argument_list|()
decl_stmt|;
name|item
operator|.
name|accept
argument_list|(
name|visitor
argument_list|)
expr_stmt|;
name|nodeCnt
operator|+=
name|visitor
operator|.
name|nodeCnt
expr_stmt|;
name|propertyCnt
operator|+=
name|visitor
operator|.
name|propertyCnt
expr_stmt|;
block|}
else|else
block|{
name|noAccess
operator|++
expr_stmt|;
block|}
block|}
name|long
name|end
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
if|if
condition|(
name|doReport
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Session "
operator|+
name|testSession
operator|.
name|getUserID
argument_list|()
operator|+
literal|" reading "
operator|+
operator|(
name|cnt
operator|-
name|noAccess
operator|)
operator|+
literal|" (Nodes: "
operator|+
name|nodeCnt
operator|+
literal|"; Properties: "
operator|+
name|propertyCnt
operator|+
literal|") completed in "
operator|+
operator|(
name|end
operator|-
name|start
operator|)
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|logout
condition|)
block|{
name|logout
argument_list|(
name|testSession
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
specifier|final
class|class
name|Visitor
extends|extends
name|TraversingItemVisitor
operator|.
name|Default
block|{
specifier|private
name|long
name|propertyCnt
decl_stmt|;
specifier|private
name|long
name|nodeCnt
decl_stmt|;
specifier|public
name|Visitor
parameter_list|()
block|{
name|super
argument_list|(
literal|false
argument_list|,
name|MAX_LEVEL
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|entering
parameter_list|(
name|Property
name|property
parameter_list|,
name|int
name|level
parameter_list|)
block|{
name|propertyCnt
operator|++
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|entering
parameter_list|(
name|Node
name|node
parameter_list|,
name|int
name|level
parameter_list|)
block|{
name|nodeCnt
operator|++
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit
