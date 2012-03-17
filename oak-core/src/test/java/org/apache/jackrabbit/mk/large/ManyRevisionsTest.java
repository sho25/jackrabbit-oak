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
name|mk
operator|.
name|large
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
name|util
operator|.
name|ArrayList
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
name|MultiMkTestBase
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
name|fast
operator|.
name|Jsop
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
name|fast
operator|.
name|JsopArray
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
name|fast
operator|.
name|JsopObject
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
name|NodeImpl
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

begin_comment
comment|/**  * Test moving nodes.  */
end_comment

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
name|ManyRevisionsTest
extends|extends
name|MultiMkTestBase
block|{
specifier|public
name|ManyRevisionsTest
parameter_list|(
name|String
name|url
parameter_list|)
block|{
name|super
argument_list|(
name|url
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|readRevisions
parameter_list|()
block|{
name|String
name|head
init|=
name|mk
operator|.
name|getHeadRevision
argument_list|()
decl_stmt|;
name|head
operator|=
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"test\" : {\"id\": -1}"
argument_list|,
name|head
argument_list|,
literal|"\"-1\""
argument_list|)
expr_stmt|;
name|String
name|first
init|=
name|head
decl_stmt|;
name|ArrayList
argument_list|<
name|String
argument_list|>
name|revs
init|=
operator|new
name|ArrayList
argument_list|<
name|String
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
literal|1000
condition|;
name|i
operator|++
control|)
block|{
name|String
name|rev
init|=
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"^ \"test/id\": "
operator|+
name|i
argument_list|,
name|head
argument_list|,
literal|"\""
operator|+
name|i
operator|+
literal|"\""
argument_list|)
decl_stmt|;
name|revs
operator|.
name|add
argument_list|(
name|rev
argument_list|)
expr_stmt|;
name|head
operator|=
name|rev
expr_stmt|;
block|}
name|int
name|i
init|=
literal|0
decl_stmt|;
name|String
name|last
init|=
name|first
decl_stmt|;
for|for
control|(
name|String
name|rev
range|:
name|revs
control|)
block|{
name|String
name|n
init|=
name|mk
operator|.
name|getNodes
argument_list|(
literal|"/test"
argument_list|,
name|rev
argument_list|)
decl_stmt|;
name|NodeImpl
name|node
init|=
name|NodeImpl
operator|.
name|parse
argument_list|(
name|n
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|i
argument_list|,
name|Integer
operator|.
name|parseInt
argument_list|(
name|node
operator|.
name|getProperty
argument_list|(
literal|"id"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|journal
init|=
name|mk
operator|.
name|getJournal
argument_list|(
name|last
argument_list|,
name|rev
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|JsopArray
name|array
init|=
operator|(
name|JsopArray
operator|)
name|Jsop
operator|.
name|parse
argument_list|(
name|journal
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|last
operator|+
literal|".."
operator|+
name|rev
operator|+
literal|": "
operator|+
name|journal
argument_list|,
literal|2
argument_list|,
name|array
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|JsopObject
name|obj
init|=
operator|(
name|JsopObject
operator|)
name|array
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"\""
operator|+
operator|(
name|i
operator|-
literal|1
operator|)
operator|+
literal|"\""
argument_list|,
name|obj
operator|.
name|get
argument_list|(
literal|"msg"
argument_list|)
argument_list|)
expr_stmt|;
name|obj
operator|=
operator|(
name|JsopObject
operator|)
name|array
operator|.
name|get
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"\""
operator|+
name|i
operator|+
literal|"\""
argument_list|,
name|obj
operator|.
name|get
argument_list|(
literal|"msg"
argument_list|)
argument_list|)
expr_stmt|;
name|last
operator|=
name|rev
expr_stmt|;
name|i
operator|++
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|smallWrites
parameter_list|()
block|{
name|String
name|head
init|=
name|mk
operator|.
name|getHeadRevision
argument_list|()
decl_stmt|;
name|log
argument_list|(
name|url
argument_list|)
expr_stmt|;
name|StopWatch
name|watch
init|=
operator|new
name|StopWatch
argument_list|()
decl_stmt|;
name|int
name|count
init|=
literal|1000
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
name|count
condition|;
name|i
operator|++
control|)
block|{
name|head
operator|=
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"^ \"x\": "
operator|+
operator|(
name|i
operator|%
literal|10
operator|)
argument_list|,
name|head
argument_list|,
literal|""
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|%
literal|100
operator|==
literal|0
operator|&&
name|watch
operator|.
name|log
argument_list|()
condition|)
block|{
name|log
argument_list|(
name|watch
operator|.
name|operationsPerSecond
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|log
argument_list|(
name|watch
operator|.
name|operationsPerSecond
argument_list|(
name|count
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|log
parameter_list|(
name|String
name|s
parameter_list|)
block|{
comment|// System.out.println(s);
block|}
block|}
end_class

end_unit

