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
name|run
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
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
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
name|io
operator|.
name|PrintStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringWriter
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
name|console
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
name|segment
operator|.
name|SegmentNodeStoreBuilders
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
name|state
operator|.
name|NodeStore
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
name|StatisticsProvider
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
name|JsonIndexTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|simple
parameter_list|()
throws|throws
name|Exception
block|{
name|assertCommand
argument_list|(
name|combineLines
argument_list|(
literal|"hello"
argument_list|)
argument_list|,
literal|"{'print':'hello'}"
argument_list|)
expr_stmt|;
name|assertCommand
argument_list|(
name|combineLines
argument_list|(
literal|"false"
argument_list|)
argument_list|,
literal|"{'print':false}"
argument_list|)
expr_stmt|;
name|assertCommand
argument_list|(
name|combineLines
argument_list|(
literal|"1"
argument_list|,
literal|"2"
argument_list|,
literal|"3"
argument_list|)
argument_list|,
literal|"{'$x':[1, 2, 3]}"
argument_list|,
literal|"{'for':'$x', 'do': [{'print': '$x'}]}"
argument_list|)
expr_stmt|;
name|assertCommand
argument_list|(
name|combineLines
argument_list|(
literal|"x1"
argument_list|,
literal|"x2"
argument_list|,
literal|"x3"
argument_list|)
argument_list|,
literal|"{'$myFunction':[{'$y': 'x', '+': '$x'}, {'print':'$y'}]}"
argument_list|,
literal|"{'$x':[1, 2, 3]}"
argument_list|,
literal|"{'for':'$x', 'do': '$myFunction'}"
argument_list|)
expr_stmt|;
name|assertCommand
argument_list|(
name|combineLines
argument_list|(
literal|"2"
argument_list|,
literal|"4"
argument_list|,
literal|"8"
argument_list|)
argument_list|,
literal|"{'$x':1}"
argument_list|,
literal|"{'loop':[{'$x': '$x', '+':'$x'}, {'print': '$x'}, {'$break': true, 'if': '$x', '=': 8}]}"
argument_list|)
expr_stmt|;
name|assertCommand
argument_list|(
name|combineLines
argument_list|(
literal|"b"
argument_list|,
literal|"d"
argument_list|)
argument_list|,
literal|"{'$x':1}"
argument_list|,
literal|"{'print':'a', 'if':'$x', '=':null}"
argument_list|,
literal|"{'print':'b', 'if':'$x', '=':1}"
argument_list|,
literal|"{'print':'c', 'if':null, '=':1}"
argument_list|,
literal|"{'print':'d', 'if':null, '=':null}"
argument_list|)
expr_stmt|;
name|assertCommand
argument_list|(
name|combineLines
argument_list|(
literal|"10"
argument_list|,
literal|"10"
argument_list|)
argument_list|,
literal|"{'$x':1}"
argument_list|,
literal|"{'$$x':10}"
argument_list|,
literal|"{'print':'$1'}"
argument_list|,
literal|"{'print':'$$x'}"
argument_list|)
expr_stmt|;
name|assertCommand
argument_list|(
name|combineLines
argument_list|(
literal|"1"
argument_list|,
literal|"null"
argument_list|,
literal|"1"
argument_list|,
literal|"2"
argument_list|,
literal|"a1"
argument_list|)
argument_list|,
literal|"{'$x':1, '+':null}"
argument_list|,
literal|"{'print':'$x'}"
argument_list|,
literal|"{'$x':null, '+':null}"
argument_list|,
literal|"{'print':'$x'}"
argument_list|,
literal|"{'$x':null, '+':1}"
argument_list|,
literal|"{'print':'$x'}"
argument_list|,
literal|"{'$x':1, '+':1}"
argument_list|,
literal|"{'print':'$x'}"
argument_list|,
literal|"{'$x':'a', '+':'1'}"
argument_list|,
literal|"{'print':'$x'}"
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|NodeStoreFixture
name|memoryFixture
parameter_list|()
block|{
return|return
operator|new
name|NodeStoreFixture
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
comment|// ignore
block|}
annotation|@
name|Override
specifier|public
name|NodeStore
name|getStore
parameter_list|()
block|{
try|try
block|{
return|return
name|SegmentNodeStoreBuilders
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
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|BlobStore
name|getBlobStore
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|StatisticsProvider
name|getStatisticsProvider
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|readWrite
parameter_list|()
throws|throws
name|Exception
block|{
name|JsonIndexCommand
name|index
init|=
operator|new
name|JsonIndexCommand
argument_list|()
decl_stmt|;
try|try
init|(
name|NodeStoreFixture
name|fixture
init|=
name|memoryFixture
argument_list|()
init|;
init|)
block|{
name|NodeStore
name|store
init|=
name|fixture
operator|.
name|getStore
argument_list|()
decl_stmt|;
name|index
operator|.
name|session
operator|=
name|JsonIndexCommand
operator|.
name|openSession
argument_list|(
name|store
argument_list|)
expr_stmt|;
name|assertCommand
argument_list|(
name|index
argument_list|,
name|combineLines
argument_list|(
literal|""
argument_list|)
argument_list|,
literal|"{'addNode':'/foo', 'node':{'jcr:primaryType': 'nt:unstructured', 'x': 1, 'y':{}}}"
argument_list|,
literal|"{'session': 'save'}"
argument_list|)
expr_stmt|;
name|assertCommand
argument_list|(
name|index
argument_list|,
name|combineLines
argument_list|(
literal|"/foo"
argument_list|,
literal|"/jcr:system"
argument_list|,
literal|"/oak:index"
argument_list|,
literal|"/rep:security"
argument_list|)
argument_list|,
literal|"{'xpath':'/jcr:root/* order by @jcr:path'}"
argument_list|)
expr_stmt|;
name|assertCommand
argument_list|(
name|index
argument_list|,
name|combineLines
argument_list|(
literal|"/oak:index/counter"
argument_list|)
argument_list|,
literal|"{'xpath':'/jcr:root//element(*, oak:QueryIndexDefinition)[@type=`counter`] "
operator|+
literal|"order by @jcr:path'}"
argument_list|)
expr_stmt|;
name|assertCommand
argument_list|(
name|index
argument_list|,
name|combineLines
argument_list|(
literal|"[nt:unstructured] as [a] /* property test = 1 "
operator|+
literal|"where ([a].[x] = 1) and (isdescendantnode([a], [/])) */"
argument_list|)
argument_list|,
literal|"{'addNode':'/oak:index/test', 'node':{ "
operator|+
literal|"'jcr:primaryType':'oak:QueryIndexDefinition', "
operator|+
literal|"'type':'property', "
operator|+
literal|"'reindex':true, "
operator|+
literal|"'entryCount': 1, "
operator|+
literal|"'{Name}declaringNodeTypes': ['nt:unstructured'], "
operator|+
literal|"'{Name}propertyNames':['x'] "
operator|+
literal|"}}"
argument_list|,
literal|"{'session':'save'}"
argument_list|,
literal|"{'xpath':'explain /jcr:root//element(*, nt:unstructured)[@x=1]'}"
argument_list|,
literal|"{'xpath':'/jcr:root//element(*, nt:unstructured)[@x=2]'}"
argument_list|)
expr_stmt|;
name|assertCommand
argument_list|(
name|index
argument_list|,
name|combineLines
argument_list|(
literal|"50"
argument_list|)
argument_list|,
literal|"{'addNode':'/foo/test', 'node':{'jcr:primaryType': 'oak:Unstructured', 'child':{}}}"
argument_list|,
literal|"{'$x':1}"
argument_list|,
literal|"{'loop':["
operator|+
literal|"{'$p': '/foo/test/child/n', '+': '$x'}, "
operator|+
literal|"{'addNode': '$p', 'node': {'x': '$x', 'jcr:primaryType': 'nt:unstructured'}}, "
operator|+
literal|"{'session':'save'}, "
operator|+
literal|"{'$x': '$x', '+':1}, "
operator|+
literal|"{'$break': true, 'if': '$x', '=': 100}]}"
argument_list|,
literal|"{'session':'save'}"
argument_list|,
literal|"{'xpath':'/jcr:root//element(*, nt:unstructured)[@x<50]', 'quiet':true}"
argument_list|,
literal|"{'$y':0}"
argument_list|,
literal|"{'for':'$result', 'do': [{'$y': '$y', '+': 1}]}"
argument_list|,
literal|"{'print': '$y'}"
argument_list|)
expr_stmt|;
name|assertCommand
argument_list|(
name|index
argument_list|,
name|combineLines
argument_list|(
literal|"[nt:unstructured] as [a] /* nodeType Filter(query="
operator|+
literal|"explain select [jcr:path], [jcr:score], * from [nt:unstructured] as a "
operator|+
literal|"where [x] = 1 and isdescendantnode(a, '/') /* xpath: "
operator|+
literal|"/jcr:root//element(*, nt:unstructured)[@x=1] */, path=//*, "
operator|+
literal|"property=[x=[1]]) where ([a].[x] = 1) and (isdescendantnode([a], [/])) */"
argument_list|)
argument_list|,
literal|"{'setProperty': '/oak:index/test/type', 'value': 'disabled'}"
argument_list|,
literal|"{'session':'save'}"
argument_list|,
literal|"{'xpath':'explain /jcr:root//element(*, nt:unstructured)[@x=1]'}"
argument_list|)
expr_stmt|;
name|assertCommand
argument_list|(
name|index
argument_list|,
name|combineLines
argument_list|(
literal|"[nt:unstructured] as [a] /* traverse '*' "
operator|+
literal|"where [a].[x] = 1 */"
argument_list|)
argument_list|,
literal|"{'removeNode': '/oak:index/nodetype'}"
argument_list|,
literal|"{'session':'save'}"
argument_list|,
literal|"{'sql':'explain select * from [nt:unstructured] as [a] where [x]=1'}"
argument_list|)
expr_stmt|;
name|assertCommand
argument_list|(
name|index
argument_list|,
name|combineLines
argument_list|(
literal|"['/foo': {\n"
operator|+
literal|"  'jcr:primaryType': 'nt:unstructured', '{Long}x': '1', 'y': {}, 'test': {}\n"
operator|+
literal|"}]"
argument_list|)
argument_list|,
literal|"{'xpath':'/jcr:root/foo', 'depth':2}"
argument_list|)
expr_stmt|;
name|index
operator|.
name|session
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
block|}
name|void
name|assertCommand
parameter_list|(
name|String
name|expected
parameter_list|,
name|String
modifier|...
name|commands
parameter_list|)
throws|throws
name|Exception
block|{
name|assertCommand
argument_list|(
operator|new
name|JsonIndexCommand
argument_list|()
argument_list|,
name|expected
argument_list|,
name|commands
argument_list|)
expr_stmt|;
block|}
name|void
name|assertCommand
parameter_list|(
name|JsonIndexCommand
name|index
parameter_list|,
name|String
name|expected
parameter_list|,
name|String
modifier|...
name|commands
parameter_list|)
throws|throws
name|Exception
block|{
name|ByteArrayOutputStream
name|w
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|PrintStream
name|out
init|=
operator|new
name|PrintStream
argument_list|(
name|w
argument_list|,
literal|false
argument_list|,
literal|"UTF-8"
argument_list|)
decl_stmt|;
name|index
operator|.
name|output
operator|=
name|out
expr_stmt|;
for|for
control|(
name|String
name|c
range|:
name|commands
control|)
block|{
name|index
operator|.
name|execute
argument_list|(
name|c
operator|.
name|replace
argument_list|(
literal|'\''
argument_list|,
literal|'"'
argument_list|)
operator|.
name|replace
argument_list|(
literal|'`'
argument_list|,
literal|'\''
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|String
name|got
init|=
operator|new
name|String
argument_list|(
name|w
operator|.
name|toByteArray
argument_list|()
argument_list|)
decl_stmt|;
name|got
operator|=
name|got
operator|.
name|trim
argument_list|()
operator|.
name|replace
argument_list|(
literal|'"'
argument_list|,
literal|'\''
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|got
argument_list|)
expr_stmt|;
block|}
specifier|static
name|String
name|combineLines
parameter_list|(
name|String
modifier|...
name|lines
parameter_list|)
block|{
name|StringWriter
name|w
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|PrintWriter
name|p
init|=
operator|new
name|PrintWriter
argument_list|(
name|w
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|l
range|:
name|lines
control|)
block|{
name|p
operator|.
name|println
argument_list|(
name|l
argument_list|)
expr_stmt|;
block|}
return|return
name|w
operator|.
name|toString
argument_list|()
operator|.
name|trim
argument_list|()
return|;
block|}
block|}
end_class

end_unit

