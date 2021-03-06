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
name|jcr
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
name|ByteArrayInputStream
import|;
end_import

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
name|List
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
name|Random
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
name|PropertyIterator
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
name|Value
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|commons
operator|.
name|JcrUtils
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
name|fixture
operator|.
name|NodeStoreFixture
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
comment|/**  * File related write operations on the repository.  */
end_comment

begin_class
specifier|public
class|class
name|ConcurrentFileOperationsTest
extends|extends
name|AbstractRepositoryTest
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|ConcurrentFileOperationsTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|NUM_WRITERS
init|=
literal|10
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|byte
index|[]
name|DATA
init|=
operator|new
name|byte
index|[
literal|8
index|]
decl_stmt|;
static|static
block|{
operator|new
name|Random
argument_list|(
literal|0
argument_list|)
operator|.
name|nextBytes
argument_list|(
name|DATA
argument_list|)
expr_stmt|;
block|}
specifier|private
name|Session
name|session
decl_stmt|;
specifier|private
name|Node
name|testRootNode
decl_stmt|;
specifier|public
name|ConcurrentFileOperationsTest
parameter_list|(
name|NodeStoreFixture
name|fixture
parameter_list|)
block|{
name|super
argument_list|(
name|fixture
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Before
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|session
operator|=
name|getAdminSession
argument_list|()
expr_stmt|;
name|testRootNode
operator|=
name|JcrUtils
operator|.
name|getOrAddNode
argument_list|(
name|session
operator|.
name|getRootNode
argument_list|()
argument_list|,
literal|"test-node"
argument_list|,
literal|"nt:unstructured"
argument_list|)
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
comment|/**      * Multiple threads create and rename files.      */
annotation|@
name|Test
specifier|public
name|void
name|concurrent
parameter_list|()
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|Session
argument_list|>
name|sessions
init|=
operator|new
name|ArrayList
argument_list|<
name|Session
argument_list|>
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
name|NUM_WRITERS
condition|;
name|i
operator|++
control|)
block|{
name|sessions
operator|.
name|add
argument_list|(
name|createAdminSession
argument_list|()
argument_list|)
expr_stmt|;
name|testRootNode
operator|.
name|addNode
argument_list|(
literal|"session-"
operator|+
name|i
argument_list|,
literal|"nt:unstructured"
argument_list|)
expr_stmt|;
block|}
name|addFile
argument_list|(
name|testRootNode
argument_list|,
literal|"dummy"
argument_list|)
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Exception
argument_list|>
name|exceptions
init|=
name|Collections
operator|.
name|synchronizedMap
argument_list|(
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Exception
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Thread
argument_list|>
name|writers
init|=
operator|new
name|ArrayList
argument_list|<
name|Thread
argument_list|>
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
name|sessions
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|Session
name|s
init|=
name|sessions
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
specifier|final
name|String
name|path
init|=
name|testRootNode
operator|.
name|getPath
argument_list|()
operator|+
literal|"/session-"
operator|+
name|i
decl_stmt|;
name|writers
operator|.
name|add
argument_list|(
operator|new
name|Thread
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|s
operator|.
name|refresh
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|Node
name|n
init|=
name|s
operator|.
name|getNode
argument_list|(
name|path
argument_list|)
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
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|String
name|tmpFile
init|=
literal|"file-"
operator|+
name|i
operator|+
literal|".tmp"
decl_stmt|;
comment|// create
name|addFile
argument_list|(
name|n
argument_list|,
name|tmpFile
argument_list|)
expr_stmt|;
name|s
operator|.
name|save
argument_list|()
expr_stmt|;
name|String
name|srcPath
init|=
name|n
operator|.
name|getPath
argument_list|()
operator|+
literal|"/"
operator|+
name|tmpFile
decl_stmt|;
name|String
name|destPath
init|=
name|n
operator|.
name|getPath
argument_list|()
operator|+
literal|"/file-"
operator|+
name|i
operator|+
literal|".bin"
decl_stmt|;
comment|// rename
name|s
operator|.
name|move
argument_list|(
name|srcPath
argument_list|,
name|destPath
argument_list|)
expr_stmt|;
name|s
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|e
parameter_list|)
block|{
name|exceptions
operator|.
name|put
argument_list|(
name|path
argument_list|,
operator|new
name|Exception
argument_list|(
name|dumpTree
argument_list|(
name|s
argument_list|,
name|path
argument_list|)
argument_list|,
name|e
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Thread
name|t
range|:
name|writers
control|)
block|{
name|t
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|Thread
name|t
range|:
name|writers
control|)
block|{
name|t
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|Session
name|s
range|:
name|sessions
control|)
block|{
name|s
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Exception
argument_list|>
name|entry
range|:
name|exceptions
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Worker ("
operator|+
name|entry
operator|.
name|getKey
argument_list|()
operator|+
literal|") failed with exception: "
operator|+
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
name|entry
operator|.
name|getValue
argument_list|()
throw|;
block|}
block|}
name|String
name|dumpTree
parameter_list|(
name|Session
name|s
parameter_list|,
name|String
name|path
parameter_list|)
block|{
specifier|final
name|StringBuilder
name|msg
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
try|try
block|{
name|s
operator|.
name|getNode
argument_list|(
name|path
argument_list|)
operator|.
name|accept
argument_list|(
operator|new
name|TraversingItemVisitor
operator|.
name|Default
argument_list|()
block|{
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
throws|throws
name|RepositoryException
block|{
name|String
name|indent
init|=
literal|""
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
name|level
condition|;
name|i
operator|++
control|)
block|{
name|indent
operator|+=
literal|"  "
expr_stmt|;
block|}
name|msg
operator|.
name|append
argument_list|(
name|indent
argument_list|)
operator|.
name|append
argument_list|(
name|node
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|PropertyIterator
name|it
init|=
name|node
operator|.
name|getProperties
argument_list|()
decl_stmt|;
name|indent
operator|+=
literal|"  "
expr_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Property
name|p
init|=
name|it
operator|.
name|nextProperty
argument_list|()
decl_stmt|;
name|msg
operator|.
name|append
argument_list|(
name|indent
argument_list|)
operator|.
name|append
argument_list|(
name|p
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|": "
argument_list|)
expr_stmt|;
if|if
condition|(
name|p
operator|.
name|isMultiple
argument_list|()
condition|)
block|{
name|msg
operator|.
name|append
argument_list|(
literal|"["
argument_list|)
expr_stmt|;
name|String
name|sep
init|=
literal|""
decl_stmt|;
name|Value
index|[]
name|values
init|=
name|p
operator|.
name|getValues
argument_list|()
decl_stmt|;
for|for
control|(
name|Value
name|value
range|:
name|values
control|)
block|{
name|msg
operator|.
name|append
argument_list|(
name|sep
argument_list|)
expr_stmt|;
name|msg
operator|.
name|append
argument_list|(
name|value
operator|.
name|getString
argument_list|()
argument_list|)
expr_stmt|;
name|sep
operator|=
literal|", "
expr_stmt|;
block|}
name|msg
operator|.
name|append
argument_list|(
literal|"]"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|msg
operator|.
name|append
argument_list|(
name|p
operator|.
name|getValue
argument_list|()
operator|.
name|getString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|msg
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|e
parameter_list|)
block|{
name|msg
operator|.
name|append
argument_list|(
name|e
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|msg
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|interleavingOperations1
parameter_list|()
throws|throws
name|Exception
block|{
name|Node
name|folder1
init|=
name|testRootNode
operator|.
name|addNode
argument_list|(
literal|"folder1"
argument_list|,
literal|"nt:unstructured"
argument_list|)
decl_stmt|;
name|Node
name|folder2
init|=
name|testRootNode
operator|.
name|addNode
argument_list|(
literal|"folder2"
argument_list|,
literal|"nt:unstructured"
argument_list|)
decl_stmt|;
name|Node
name|file1
init|=
name|addFile
argument_list|(
name|session
operator|.
name|getNode
argument_list|(
name|folder1
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|,
literal|"file1.tmp"
argument_list|)
decl_stmt|;
name|Node
name|file2
init|=
name|addFile
argument_list|(
name|session
operator|.
name|getNode
argument_list|(
name|folder2
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|,
literal|"file2.tmp"
argument_list|)
decl_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
name|Session
name|s1
init|=
name|createAdminSession
argument_list|()
decl_stmt|;
name|Session
name|s2
init|=
name|createAdminSession
argument_list|()
decl_stmt|;
try|try
block|{
name|rename
argument_list|(
name|s1
operator|.
name|getNode
argument_list|(
name|file1
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|,
literal|"file1.bin"
argument_list|)
expr_stmt|;
name|rename
argument_list|(
name|s2
operator|.
name|getNode
argument_list|(
name|file2
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|,
literal|"file2.bin"
argument_list|)
expr_stmt|;
name|s1
operator|.
name|save
argument_list|()
expr_stmt|;
name|s2
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|s1
operator|.
name|logout
argument_list|()
expr_stmt|;
name|s2
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|interleavingOperations2
parameter_list|()
throws|throws
name|Exception
block|{
name|Node
name|folder1
init|=
name|testRootNode
operator|.
name|addNode
argument_list|(
literal|"folder1"
argument_list|,
literal|"nt:unstructured"
argument_list|)
decl_stmt|;
name|Node
name|folder2
init|=
name|testRootNode
operator|.
name|addNode
argument_list|(
literal|"folder2"
argument_list|,
literal|"nt:unstructured"
argument_list|)
decl_stmt|;
name|addFile
argument_list|(
name|testRootNode
argument_list|,
literal|"dummy"
argument_list|)
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
name|Session
name|s1
init|=
name|createAdminSession
argument_list|()
decl_stmt|;
name|Session
name|s2
init|=
name|createAdminSession
argument_list|()
decl_stmt|;
try|try
block|{
name|Node
name|file1
init|=
name|addFile
argument_list|(
name|s1
operator|.
name|getNode
argument_list|(
name|folder1
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|,
literal|"file1.tmp"
argument_list|)
decl_stmt|;
name|Node
name|file2
init|=
name|addFile
argument_list|(
name|s2
operator|.
name|getNode
argument_list|(
name|folder2
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|,
literal|"file2.tmp"
argument_list|)
decl_stmt|;
name|s1
operator|.
name|save
argument_list|()
expr_stmt|;
name|s2
operator|.
name|save
argument_list|()
expr_stmt|;
name|rename
argument_list|(
name|file1
argument_list|,
literal|"file1.bin"
argument_list|)
expr_stmt|;
name|rename
argument_list|(
name|file2
argument_list|,
literal|"file2.bin"
argument_list|)
expr_stmt|;
name|s1
operator|.
name|save
argument_list|()
expr_stmt|;
name|s2
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|s1
operator|.
name|logout
argument_list|()
expr_stmt|;
name|s2
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
name|session
operator|.
name|refresh
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|session
operator|.
name|nodeExists
argument_list|(
name|folder1
operator|.
name|getPath
argument_list|()
operator|+
literal|"/file1.bin"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|session
operator|.
name|nodeExists
argument_list|(
name|folder1
operator|.
name|getPath
argument_list|()
operator|+
literal|"/file1.tmp"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|session
operator|.
name|nodeExists
argument_list|(
name|folder2
operator|.
name|getPath
argument_list|()
operator|+
literal|"/file2.bin"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|session
operator|.
name|nodeExists
argument_list|(
name|folder2
operator|.
name|getPath
argument_list|()
operator|+
literal|"/file2.tmp"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|Node
name|addFile
parameter_list|(
name|Node
name|parent
parameter_list|,
name|String
name|name
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|JcrUtils
operator|.
name|putFile
argument_list|(
name|parent
argument_list|,
name|name
argument_list|,
literal|"application/octet-stream"
argument_list|,
operator|new
name|ByteArrayInputStream
argument_list|(
name|DATA
argument_list|)
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|void
name|rename
parameter_list|(
name|Node
name|node
parameter_list|,
name|String
name|name
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|String
name|destPath
init|=
name|PathUtils
operator|.
name|getParentPath
argument_list|(
name|node
operator|.
name|getPath
argument_list|()
argument_list|)
operator|+
literal|"/"
operator|+
name|name
decl_stmt|;
name|node
operator|.
name|getSession
argument_list|()
operator|.
name|move
argument_list|(
name|node
operator|.
name|getPath
argument_list|()
argument_list|,
name|destPath
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

