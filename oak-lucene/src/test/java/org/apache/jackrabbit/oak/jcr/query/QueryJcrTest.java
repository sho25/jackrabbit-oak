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
name|FulltextQueryTest
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
name|FulltextSQL2QueryTest
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
name|JoinTest
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
name|MixinTest
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
name|SQL2NodeLocalNameTest
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
name|SQL2OuterJoinTest
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
name|test
operator|.
name|ConcurrentTestSuite
import|;
end_import

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
literal|"Jackrabbit query tests using a Lucene based index"
argument_list|)
decl_stmt|;
name|suite
operator|.
name|addTestSuite
argument_list|(
name|FulltextQueryTest
operator|.
name|class
argument_list|)
expr_stmt|;
name|suite
operator|.
name|addTestSuite
argument_list|(
name|SQLTest
operator|.
name|class
argument_list|)
expr_stmt|;
name|suite
operator|.
name|addTestSuite
argument_list|(
name|JoinTest
operator|.
name|class
argument_list|)
expr_stmt|;
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
name|suite
operator|.
name|addTestSuite
argument_list|(
name|FulltextSQL2QueryTest
operator|.
name|class
argument_list|)
expr_stmt|;
name|suite
operator|.
name|addTestSuite
argument_list|(
name|SQL2NodeLocalNameTest
operator|.
name|class
argument_list|)
expr_stmt|;
name|suite
operator|.
name|addTestSuite
argument_list|(
name|MixinTest
operator|.
name|class
argument_list|)
expr_stmt|;
name|suite
operator|.
name|addTestSuite
argument_list|(
name|SQL2OuterJoinTest
operator|.
name|class
argument_list|)
expr_stmt|;
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
comment|// FAILURES
comment|//
comment|//         suite.addTestSuite(SQL2OrderByTest.class); // order by score is not stable
comment|// suite.addTestSuite(QueryResultTest.class); // OAK-484
comment|// suite.addTestSuite(OrderByTest.class); // OAK-466
comment|// suite.addTestSuite(ParentNodeTest.class); // OAK-309
comment|// suite.addTestSuite(ExcerptTest.class); // OAK-318
comment|// suite.addTestSuite(SimilarQueryTest.class); // OAK-319
comment|// suite.addTestSuite(DerefTest.class); // OAK-321
comment|// suite.addTestSuite(XPathAxisTest.class); // OAK-322
comment|// suite.addTestSuite(SQL2QueryResultTest.class); // OAK-323
comment|// suite.addTestSuite(SimpleQueryTest.class); // OAK-327
comment|// suite.addTestSuite(FnNameQueryTest.class); // OAK-328
comment|// suite.addTestSuite(UpperLowerCaseQueryTest.class); // OAK-329
comment|// suite.addTestSuite(SQL2PathEscapingTest.class); // OAK-481
comment|// NOT IMPLEMENTED
comment|//
comment|// suite.addTestSuite(ChildAxisQueryTest.class); // sns
comment|// suite.addTestSuite(SelectClauseTest.class); // sns
comment|// suite.addTestSuite(ShareableNodeTest.class); // ws#clone
comment|// suite.addTestSuite(VersionStoreQueryTest.class); // versioning
comment|// TOO JR SPECIFIC
comment|//
comment|// suite.addTestSuite(LimitedAccessQueryTest.class); // acls
comment|// suite.addTestSuite(SkipDeniedNodesTest.class); // acls
return|return
name|suite
return|;
block|}
block|}
end_class

end_unit

