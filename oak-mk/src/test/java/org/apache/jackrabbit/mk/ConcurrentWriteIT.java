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
name|core
operator|.
name|MicroKernelImpl
import|;
end_import

begin_class
specifier|public
class|class
name|ConcurrentWriteIT
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
name|ConcurrentWriteIT
operator|.
name|class
operator|.
name|getName
argument_list|()
decl_stmt|;
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
specifier|final
name|MicroKernel
name|mk
init|=
operator|new
name|MicroKernelImpl
argument_list|()
decl_stmt|;
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
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
literal|null
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
block|{     }
comment|/**      * Runs the test.      */
specifier|public
name|void
name|testConcurrentWriting
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|oldHead
init|=
name|mk
operator|.
name|getHeadRevision
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
name|threads
index|[
name|i
index|]
operator|=
name|thread
expr_stmt|;
name|assertFalse
argument_list|(
name|mk
operator|.
name|nodeExists
argument_list|(
name|TEST_PATH
operator|+
literal|"/"
operator|+
name|thread
operator|.
name|getName
argument_list|()
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// long t0 = System.currentTimeMillis();
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
name|start
argument_list|()
expr_stmt|;
block|}
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
comment|// long t1 = System.currentTimeMillis();
comment|// System.out.println("duration: " + (t1 - t0) + "ms");
for|for
control|(
name|Thread
name|t
range|:
name|threads
control|)
block|{
name|assertTrue
argument_list|(
name|mk
operator|.
name|nodeExists
argument_list|(
name|TEST_PATH
operator|+
literal|"/"
operator|+
name|t
operator|.
name|getName
argument_list|()
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
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

