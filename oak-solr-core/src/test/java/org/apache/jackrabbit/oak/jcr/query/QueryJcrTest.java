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
operator|.
name|query
package|;
end_package

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|Test
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
name|junit
operator|.
name|framework
operator|.
name|TestSuite
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
name|core
operator|.
name|query
operator|.
name|LimitAndOffsetTest
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
name|core
operator|.
name|query
operator|.
name|PathQueryNodeTest
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
name|core
operator|.
name|query
operator|.
name|SQL2OffsetLimitTest
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
name|core
operator|.
name|query
operator|.
name|SQL2QueryResultTest
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
name|core
operator|.
name|query
operator|.
name|SQLTest
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
name|core
operator|.
name|query
operator|.
name|SkipDeletedNodesTest
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
name|core
operator|.
name|query
operator|.
name|VersionStoreQueryTest
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
name|test
operator|.
name|ConcurrentTestSuite
import|;
end_import

begin_comment
comment|/**  * Test suite of JCR tests to be run with Oak Solr index  */
end_comment

begin_class
specifier|public
class|class
name|QueryJcrTest
extends|extends
name|TestCase
block|{
specifier|public
specifier|static
name|Test
name|suite
parameter_list|()
block|{
name|TestSuite
name|suite
init|=
operator|new
name|ConcurrentTestSuite
argument_list|(
literal|"Jackrabbit query tests using a Solr based index"
argument_list|)
decl_stmt|;
comment|//        suite.addTestSuite(FulltextQueryTest.class); // fail
name|suite
operator|.
name|addTestSuite
argument_list|(
name|SQLTest
operator|.
name|class
argument_list|)
expr_stmt|;
comment|//        suite.addTestSuite(JoinTest.class); // fail
name|suite
operator|.
name|addTestSuite
argument_list|(
name|SkipDeletedNodesTest
operator|.
name|class
argument_list|)
expr_stmt|;
name|suite
operator|.
name|addTestSuite
argument_list|(
name|PathQueryNodeTest
operator|.
name|class
argument_list|)
expr_stmt|;
comment|//        suite.addTestSuite(FulltextSQL2QueryTest.class); // fail
comment|//        suite.addTestSuite(SQL2NodeLocalNameTest.class); // fail
comment|//        suite.addTestSuite(SQL2OrderByTest.class); // fail
comment|//        suite.addTestSuite(MixinTest.class); // fail
comment|//        suite.addTestSuite(SQL2OuterJoinTest.class);
name|suite
operator|.
name|addTestSuite
argument_list|(
name|SQL2OffsetLimitTest
operator|.
name|class
argument_list|)
expr_stmt|;
name|suite
operator|.
name|addTestSuite
argument_list|(
name|LimitAndOffsetTest
operator|.
name|class
argument_list|)
expr_stmt|;
comment|//        suite.addTestSuite(OrderByTest.class); // fail
comment|//        suite.addTestSuite(ExcerptTest.class); // error unsupported
comment|//        suite.addTestSuite(QueryResultTest.class); // fail
comment|//        suite.addTestSuite(ParentNodeTest.class);  // fail
comment|//        suite.addTestSuite(SimilarQueryTest.class); // error unsupported
comment|//        suite.addTestSuite(DerefTest.class); // error
comment|//        suite.addTestSuite(XPathAxisTest.class); // fail and error
name|suite
operator|.
name|addTestSuite
argument_list|(
name|SQL2QueryResultTest
operator|.
name|class
argument_list|)
expr_stmt|;
comment|//        suite.addTestSuite(SimpleQueryTest.class); // fail and error
comment|//        suite.addTestSuite(FnNameQueryTest.class); // fail
comment|//        suite.addTestSuite(UpperLowerCaseQueryTest.class); // fail
comment|//        suite.addTestSuite(SQL2PathEscapingTest.class); // fail and error
comment|//        suite.addTestSuite(ChildAxisQueryTest.class); // fail and error : javax.jcr.ItemExistsException: node3
comment|//        suite.addTestSuite(SelectClauseTest.class); // error : javax.jcr.ItemExistsException: node
comment|//        suite.addTestSuite(ShareableNodeTest.class); //not implemented
name|suite
operator|.
name|addTestSuite
argument_list|(
name|VersionStoreQueryTest
operator|.
name|class
argument_list|)
expr_stmt|;
return|return
name|suite
return|;
block|}
block|}
end_class

end_unit

