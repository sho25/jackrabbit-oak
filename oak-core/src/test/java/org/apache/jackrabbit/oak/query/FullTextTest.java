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
name|query
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
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|ParseException
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
name|query
operator|.
name|ast
operator|.
name|FullTextSearchImpl
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
comment|/**  * Test the fulltext parsing and evaluation.  */
end_comment

begin_class
specifier|public
class|class
name|FullTextTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|and
parameter_list|()
throws|throws
name|ParseException
block|{
name|assertFalse
argument_list|(
name|test
argument_list|(
literal|"hello world"
argument_list|,
literal|"hello"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|test
argument_list|(
literal|"hello world"
argument_list|,
literal|"world"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|test
argument_list|(
literal|"hello world"
argument_list|,
literal|"world hello"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|test
argument_list|(
literal|"hello world "
argument_list|,
literal|"hello world"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|or
parameter_list|()
throws|throws
name|ParseException
block|{
name|assertTrue
argument_list|(
name|test
argument_list|(
literal|"hello OR world"
argument_list|,
literal|"hello"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|test
argument_list|(
literal|"hello OR world"
argument_list|,
literal|"world"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|test
argument_list|(
literal|"hello OR world"
argument_list|,
literal|"hi"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|not
parameter_list|()
throws|throws
name|ParseException
block|{
name|assertTrue
argument_list|(
name|test
argument_list|(
literal|"hello -world"
argument_list|,
literal|"hello"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|test
argument_list|(
literal|"hello -world"
argument_list|,
literal|"hello world"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|quoted
parameter_list|()
throws|throws
name|ParseException
block|{
name|assertTrue
argument_list|(
name|test
argument_list|(
literal|"\"hello world\""
argument_list|,
literal|"hello world"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|test
argument_list|(
literal|"\"hello world\""
argument_list|,
literal|"world hello"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|test
argument_list|(
literal|"\"hello-world\""
argument_list|,
literal|"hello-world"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|test
argument_list|(
literal|"\"hello\\-world\""
argument_list|,
literal|"hello-world"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|test
argument_list|(
literal|"\"hello \\\"world\\\"\""
argument_list|,
literal|"hello \"world\""
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|test
argument_list|(
literal|"\"hello world\" -hallo"
argument_list|,
literal|"hello world"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|test
argument_list|(
literal|"\"hello world\" -hallo"
argument_list|,
literal|"hallo hello world"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|escaped
parameter_list|()
throws|throws
name|ParseException
block|{
name|assertFalse
argument_list|(
name|test
argument_list|(
literal|"\\\"hello\\\""
argument_list|,
literal|"hello"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|test
argument_list|(
literal|"\"hello\""
argument_list|,
literal|"\"hello\""
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|test
argument_list|(
literal|"\\\"hello\\\""
argument_list|,
literal|"\"hello\""
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|test
argument_list|(
literal|"\\-1 2 3"
argument_list|,
literal|"1 2 3"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|test
argument_list|(
literal|"\\-1 2 3"
argument_list|,
literal|"-1 2 3"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|invalid
parameter_list|()
throws|throws
name|ParseException
block|{
name|testInvalid
argument_list|(
literal|""
argument_list|,
literal|"(*); expected: term"
argument_list|)
expr_stmt|;
name|testInvalid
argument_list|(
literal|"x OR "
argument_list|,
literal|"x OR(*); expected: term"
argument_list|)
expr_stmt|;
name|testInvalid
argument_list|(
literal|"\""
argument_list|,
literal|"(*)\"; expected: double quote"
argument_list|)
expr_stmt|;
name|testInvalid
argument_list|(
literal|"-"
argument_list|,
literal|"(*)-; expected: term"
argument_list|)
expr_stmt|;
name|testInvalid
argument_list|(
literal|"- x"
argument_list|,
literal|"- (*)x; expected: term"
argument_list|)
expr_stmt|;
name|testInvalid
argument_list|(
literal|"\\"
argument_list|,
literal|"(*)\\; expected: escaped char"
argument_list|)
expr_stmt|;
name|testInvalid
argument_list|(
literal|"\"\\"
argument_list|,
literal|"\"(*)\\; expected: escaped char"
argument_list|)
expr_stmt|;
name|testInvalid
argument_list|(
literal|"\"x\"y"
argument_list|,
literal|"\"x\"(*)y; expected: space"
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|testInvalid
parameter_list|(
name|String
name|pattern
parameter_list|,
name|String
name|expectedMessage
parameter_list|)
block|{
try|try
block|{
name|test
argument_list|(
name|pattern
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expected exception "
operator|+
name|expectedMessage
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|e
parameter_list|)
block|{
name|String
name|msg
init|=
name|e
operator|.
name|getMessage
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|msg
operator|.
name|startsWith
argument_list|(
literal|"FullText expression: "
argument_list|)
argument_list|)
expr_stmt|;
name|msg
operator|=
name|msg
operator|.
name|substring
argument_list|(
literal|"FullText expression: "
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expectedMessage
argument_list|,
name|msg
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|boolean
name|test
parameter_list|(
name|String
name|pattern
parameter_list|,
name|String
name|value
parameter_list|)
throws|throws
name|ParseException
block|{
name|FullTextSearchImpl
operator|.
name|FullTextExpression
name|e
init|=
name|FullTextSearchImpl
operator|.
name|FullTextParser
operator|.
name|parse
argument_list|(
name|pattern
argument_list|)
decl_stmt|;
return|return
name|e
operator|.
name|evaluate
argument_list|(
name|value
argument_list|)
return|;
block|}
block|}
end_class

end_unit

