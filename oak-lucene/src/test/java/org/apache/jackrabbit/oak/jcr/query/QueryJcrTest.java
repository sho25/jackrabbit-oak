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
comment|// suite.addTestSuite(FulltextQueryTest.class);
comment|// ok but I'm not too sure about
comment|// OAK-348 - fulltext tokenization
name|suite
operator|.
name|addTestSuite
argument_list|(
name|SQLTest
operator|.
name|class
argument_list|)
expr_stmt|;
comment|// ok
comment|// suite.addTestSuite(SimpleQueryTest.class);
comment|// OAK-327 eq support
comment|// like pattern escaped
comment|// type support
comment|// suite.addTestSuite(UpperLowerCaseQueryTest.class);
comment|// OAK-329 type check should fail but doesn't
comment|// suite.addTestSuite(QueryResultTest.class);
comment|// OAK-308
comment|// type checks *probably* fail some of the tests
comment|// some orderby conditions
comment|// suite.addTestSuite(SQL2QueryResultTest.class);
comment|// OAK-323 - column names
comment|// suite.addTestSuite(SQL2OuterJoinTest.class); // ok
comment|// suite.addTestSuite(SkipDeletedNodesTest.class); // ok
comment|// suite.addTestSuite(PathQueryNodeTest.class); // ok
comment|// suite.addTestSuite(FulltextSQL2QueryTest.class); // ok
comment|// suite.addTestSuite(SQL2NodeLocalNameTest.class); // ok
comment|// suite.addTestSuite(SQL2OffsetLimitTest.class); // ok
comment|// suite.addTestSuite(SQL2OrderByTest.class); // ok
comment|// suite.addTestSuite(MixinTest.class);// ok
comment|// suite.addTestSuite(JoinTest.class); // ok
comment|//
comment|// NOT OK
comment|//
comment|// suite.addTestSuite(OrderByTest.class); // OAK-347
comment|// suite.addTestSuite(SQL2PathEscapingTest.class); // OAK-295
comment|// suite.addTestSuite(LimitAndOffsetTest.class); // OAK-308
comment|// suite.addTestSuite(ParentNodeTest.class); // OAK-309
comment|// suite.addTestSuite(XPathAxisTest.class); // OAK-322
comment|// suite.addTestSuite(FnNameQueryTest.class);
comment|// OAK-328: "%:content" illegal name
comment|//
comment|// NOT IMPLEMENTED
comment|//
comment|// suite.addTestSuite(DerefTest.class); // OAK-321
comment|// suite.addTestSuite(ExcerptTest.class); // OAK-318
comment|// suite.addTestSuite(SimilarQueryTest.class); // OAK-319
comment|// suite.addTestSuite(ChildAxisQueryTest.class); // sns
comment|// suite.addTestSuite(SelectClauseTest.class); // sns
comment|// suite.addTestSuite(ShareableNodeTest.class); // ws#clone
comment|// suite.addTestSuite(VersionStoreQueryTest.class); // versioning
comment|//
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

