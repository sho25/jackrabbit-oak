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
name|mongomk
operator|.
name|impl
package|;
end_package

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
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|mongomk
operator|.
name|AbstractMongoConnectionTest
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
name|junit
operator|.
name|Ignore
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

begin_comment
comment|/**  * FIXME - Look into these tests and see if we want to fix them somehow.  *  * Tests for MongoMicroKernel limits.  */
end_comment

begin_class
specifier|public
class|class
name|MongoMKLimitsTest
extends|extends
name|AbstractMongoConnectionTest
block|{
comment|/**      * This test currently fails due to 1000 char limit in property sizes in      * MongoDB which affects path property. It also slows down as the test      * progresses.      */
annotation|@
name|Test
annotation|@
name|Ignore
specifier|public
name|void
name|pathLimit
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|path
init|=
literal|"/"
decl_stmt|;
name|String
name|baseNodeName
init|=
literal|"testingtestingtesting"
decl_stmt|;
name|int
name|numberOfCommits
init|=
literal|100
decl_stmt|;
name|String
name|jsonDiff
decl_stmt|;
name|String
name|message
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
name|numberOfCommits
condition|;
name|i
operator|++
control|)
block|{
name|jsonDiff
operator|=
literal|"+\""
operator|+
name|baseNodeName
operator|+
name|i
operator|+
literal|"\" : {}"
expr_stmt|;
name|message
operator|=
literal|"Add node n"
operator|+
name|i
expr_stmt|;
name|mk
operator|.
name|commit
argument_list|(
name|path
argument_list|,
name|jsonDiff
argument_list|,
literal|null
argument_list|,
name|message
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|PathUtils
operator|.
name|denotesRoot
argument_list|(
name|path
argument_list|)
condition|)
block|{
name|path
operator|+=
literal|"/"
expr_stmt|;
block|}
name|path
operator|+=
name|baseNodeName
operator|+
name|i
expr_stmt|;
block|}
block|}
comment|/**      * This currently fails due to 16MB DBObject size limitation from Mongo      * database.      */
annotation|@
name|Test
annotation|@
name|Ignore
specifier|public
name|void
name|overMaxBSONLimit
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|path
init|=
literal|"/"
decl_stmt|;
name|String
name|baseNodeName
init|=
literal|"N"
decl_stmt|;
name|StringBuilder
name|jsonDiff
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|String
name|message
decl_stmt|;
comment|// create a 1 MB property
name|char
index|[]
name|chars
init|=
operator|new
name|char
index|[
literal|1024
operator|*
literal|1024
index|]
decl_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|chars
argument_list|,
literal|'0'
argument_list|)
expr_stmt|;
name|String
name|content
init|=
operator|new
name|String
argument_list|(
name|chars
argument_list|)
decl_stmt|;
comment|// create 16+ MB diff
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|16
condition|;
name|i
operator|++
control|)
block|{
name|jsonDiff
operator|.
name|append
argument_list|(
literal|"+\""
operator|+
name|baseNodeName
operator|+
name|i
operator|+
literal|"\" : {\"key\":\""
operator|+
name|content
operator|+
literal|"\"}\n"
argument_list|)
expr_stmt|;
block|}
name|String
name|diff
init|=
name|jsonDiff
operator|.
name|toString
argument_list|()
decl_stmt|;
name|message
operator|=
literal|"Commit diff size "
operator|+
name|diff
operator|.
name|getBytes
argument_list|()
operator|.
name|length
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|mk
operator|.
name|commit
argument_list|(
name|path
argument_list|,
name|diff
argument_list|,
literal|null
argument_list|,
name|message
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

