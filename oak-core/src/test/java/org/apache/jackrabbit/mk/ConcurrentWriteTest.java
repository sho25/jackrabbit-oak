begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|mk
package|;
end_package

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
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
name|MicroKernel
import|;
end_import

begin_class
specifier|public
class|class
name|ConcurrentWriteTest
extends|extends
name|TestCase
block|{
specifier|protected
specifier|static
specifier|final
name|String
name|TEST_PATH
init|=
literal|"/"
operator|+
name|ConcurrentWriteTest
operator|.
name|class
operator|.
name|getName
argument_list|()
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|URL
init|=
literal|"fs:{homeDir}/target;clean"
decl_stmt|;
comment|// private static final String URL = "fs:{homeDir}/target";
comment|// private static final String URL = "simple:";
comment|//private static final String URL = "simple:fs:target/temp;clean";
specifier|private
specifier|static
specifier|final
name|int
name|NUM_THREADS
init|=
literal|20
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|NUM_CHILDNODES
init|=
literal|1000
decl_stmt|;
name|MicroKernel
name|mk
decl_stmt|;
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|mk
operator|=
name|MicroKernelFactory
operator|.
name|getInstance
argument_list|(
name|URL
argument_list|)
expr_stmt|;
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+ \""
operator|+
name|TEST_PATH
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
operator|+
literal|"\": {\"jcr:primaryType\":\"nt:unstructured\"}"
argument_list|,
name|mk
operator|.
name|getHeadRevision
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|String
name|head
init|=
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"- \""
operator|+
name|TEST_PATH
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
operator|+
literal|"\""
argument_list|,
name|mk
operator|.
name|getHeadRevision
argument_list|()
argument_list|,
literal|null
argument_list|)
decl_stmt|;
comment|//System.out.println("new HEAD: " + head);
name|mk
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
comment|/**      * Runs the test.      */
specifier|public
name|void
name|testConcurrentWriting
parameter_list|()
throws|throws
name|Exception
block|{
name|Profiler
name|prof
init|=
operator|new
name|Profiler
argument_list|()
decl_stmt|;
name|prof
operator|.
name|depth
operator|=
literal|8
expr_stmt|;
name|prof
operator|.
name|interval
operator|=
literal|1
expr_stmt|;
comment|// prof.startCollecting();
name|String
name|oldHead
init|=
name|mk
operator|.
name|getHeadRevision
argument_list|()
decl_stmt|;
name|long
name|t0
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|TestThread
index|[]
name|threads
init|=
operator|new
name|TestThread
index|[
name|NUM_THREADS
index|]
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
name|threads
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|TestThread
name|thread
init|=
operator|new
name|TestThread
argument_list|(
name|oldHead
argument_list|,
literal|"t"
operator|+
name|i
argument_list|)
decl_stmt|;
name|thread
operator|.
name|start
argument_list|()
expr_stmt|;
name|threads
index|[
name|i
index|]
operator|=
name|thread
expr_stmt|;
block|}
for|for
control|(
name|TestThread
name|t
range|:
name|threads
control|)
block|{
if|if
condition|(
name|t
operator|!=
literal|null
condition|)
block|{
name|t
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
block|}
name|long
name|t1
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"duration: "
operator|+
operator|(
name|t1
operator|-
name|t0
operator|)
operator|+
literal|"ms"
argument_list|)
expr_stmt|;
name|String
name|head
init|=
name|mk
operator|.
name|getHeadRevision
argument_list|()
decl_stmt|;
name|mk
operator|.
name|getNodes
argument_list|(
literal|"/"
argument_list|,
name|head
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
literal|0
argument_list|,
operator|-
literal|1
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// System.out.println(json);
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"new HEAD: "
operator|+
name|head
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|()
expr_stmt|;
name|String
name|history
init|=
name|mk
operator|.
name|getRevisions
argument_list|(
name|t0
argument_list|,
operator|-
literal|1
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"History:"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|history
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|()
expr_stmt|;
name|mk
operator|.
name|getJournal
argument_list|(
name|oldHead
argument_list|,
name|head
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// System.out.println("Journal:");
comment|// System.out.println(journal);
comment|// System.out.println();
comment|// System.out.println(prof.getTop(5));
block|}
class|class
name|TestThread
extends|extends
name|Thread
block|{
name|String
name|revId
decl_stmt|;
name|Random
name|rand
decl_stmt|;
name|TestThread
parameter_list|(
name|String
name|revId
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|this
operator|.
name|revId
operator|=
name|revId
expr_stmt|;
name|rand
operator|=
operator|new
name|Random
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|run
parameter_list|()
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"+\""
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"\" : {\"jcr:primaryType\":\"nt:unstructured\",\n"
argument_list|)
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
name|NUM_CHILDNODES
condition|;
name|i
operator|++
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"\"sub"
operator|+
name|i
operator|+
literal|"\" : {\"jcr:primaryType\":\"nt:unstructured\", \"prop\":\""
operator|+
name|rand
operator|.
name|nextLong
argument_list|()
operator|+
literal|"\"}"
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|==
name|NUM_CHILDNODES
operator|-
literal|1
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sb
operator|.
name|append
argument_list|(
literal|",\n"
argument_list|)
expr_stmt|;
block|}
block|}
name|sb
operator|.
name|append
argument_list|(
literal|'}'
argument_list|)
expr_stmt|;
name|revId
operator|=
name|mk
operator|.
name|commit
argument_list|(
name|TEST_PATH
argument_list|,
name|sb
operator|.
name|toString
argument_list|()
argument_list|,
name|revId
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

