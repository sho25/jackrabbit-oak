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
name|json
package|;
end_package

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
name|util
operator|.
name|StopWatch
import|;
end_import

begin_comment
comment|/**  * Test the Jsop tokenizer and builder.  */
end_comment

begin_class
specifier|public
class|class
name|JsopTest
extends|extends
name|TestCase
block|{
comment|// run the micro-benchmark
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
modifier|...
name|args
parameter_list|)
block|{
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
name|k
operator|<
literal|5
condition|;
name|k
operator|++
control|)
block|{
comment|// String s = "Hello World Hello World Hello World Hello World Hello World Hello World ";
name|String
name|s
init|=
literal|"Hello \"World\" Hello \"World\" Hello \"World\" Hello \"World\" Hello \"World\" Hello \"World\" "
decl_stmt|;
name|StopWatch
name|timer
init|=
operator|new
name|StopWatch
argument_list|()
decl_stmt|;
name|int
name|t2
init|=
literal|0
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
literal|10000000
condition|;
name|i
operator|++
control|)
block|{
name|t2
operator|+=
name|JsopBuilder
operator|.
name|encode
argument_list|(
name|s
argument_list|)
operator|.
name|length
argument_list|()
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|timer
operator|.
name|seconds
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// old: not escaped: 5691 ms; escaped: 10609 ms
comment|// new: not escaped: 3931 ms; escaped: 11001 ms
block|}
specifier|public
name|void
name|testDataType
parameter_list|()
block|{
name|String
name|dateString
init|=
operator|new
name|JsopBuilder
argument_list|()
operator|.
name|key
argument_list|(
literal|"string"
argument_list|)
operator|.
name|value
argument_list|(
literal|"/Date(0)/"
argument_list|)
operator|.
name|key
argument_list|(
literal|"date"
argument_list|)
operator|.
name|encodedValue
argument_list|(
literal|"\"\\/Date(0)\\/\""
argument_list|)
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"\"string\":\"/Date(0)/\","
operator|+
literal|"\"date\":\"\\/Date(0)\\/\""
argument_list|,
name|dateString
argument_list|)
expr_stmt|;
name|JsopTokenizer
name|t
init|=
operator|new
name|JsopTokenizer
argument_list|(
name|dateString
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"string"
argument_list|,
name|t
operator|.
name|readString
argument_list|()
argument_list|)
expr_stmt|;
name|t
operator|.
name|read
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/Date(0)/"
argument_list|,
name|t
operator|.
name|readString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/Date(0)/"
argument_list|,
name|t
operator|.
name|getEscapedToken
argument_list|()
argument_list|)
expr_stmt|;
name|t
operator|.
name|read
argument_list|(
literal|','
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"date"
argument_list|,
name|t
operator|.
name|readString
argument_list|()
argument_list|)
expr_stmt|;
name|t
operator|.
name|read
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/Date(0)/"
argument_list|,
name|t
operator|.
name|readString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"\\/Date(0)\\/"
argument_list|,
name|t
operator|.
name|getEscapedToken
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testNull
parameter_list|()
block|{
name|JsopTokenizer
name|t
init|=
operator|new
name|JsopTokenizer
argument_list|(
literal|"null"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|t
operator|.
name|read
argument_list|(
name|JsopTokenizer
operator|.
name|NULL
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testLineLength
parameter_list|()
block|{
name|JsopBuilder
name|buff
init|=
operator|new
name|JsopBuilder
argument_list|()
decl_stmt|;
name|buff
operator|.
name|key
argument_list|(
literal|"hello"
argument_list|)
operator|.
name|value
argument_list|(
literal|"world"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"\"hello\":\"world\""
argument_list|,
name|buff
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|15
argument_list|,
name|buff
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|buff
operator|=
operator|new
name|JsopBuilder
argument_list|()
expr_stmt|;
name|buff
operator|.
name|setLineLength
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|buff
operator|.
name|key
argument_list|(
literal|"hello"
argument_list|)
operator|.
name|value
argument_list|(
literal|"world"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"\"hello\":\n\"world\""
argument_list|,
name|buff
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|16
argument_list|,
name|buff
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testNumber
parameter_list|()
block|{
name|JsopTokenizer
name|t
init|=
operator|new
name|JsopTokenizer
argument_list|(
literal|"9/3:-3-:-/- 3"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"9"
argument_list|,
name|t
operator|.
name|read
argument_list|(
name|JsopTokenizer
operator|.
name|NUMBER
argument_list|)
argument_list|)
expr_stmt|;
name|t
operator|.
name|read
argument_list|(
literal|'/'
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"3"
argument_list|,
name|t
operator|.
name|read
argument_list|(
name|JsopTokenizer
operator|.
name|NUMBER
argument_list|)
argument_list|)
expr_stmt|;
name|t
operator|.
name|read
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"-3"
argument_list|,
name|t
operator|.
name|read
argument_list|(
name|JsopTokenizer
operator|.
name|NUMBER
argument_list|)
argument_list|)
expr_stmt|;
name|t
operator|.
name|read
argument_list|(
literal|'-'
argument_list|)
expr_stmt|;
name|t
operator|.
name|read
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
name|t
operator|.
name|read
argument_list|(
literal|'-'
argument_list|)
expr_stmt|;
name|t
operator|.
name|read
argument_list|(
literal|'/'
argument_list|)
expr_stmt|;
name|t
operator|.
name|read
argument_list|(
literal|'-'
argument_list|)
expr_stmt|;
name|t
operator|.
name|read
argument_list|(
name|JsopTokenizer
operator|.
name|NUMBER
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testRawValue
parameter_list|()
block|{
name|JsopTokenizer
name|t
init|=
operator|new
name|JsopTokenizer
argument_list|(
literal|"{\"x\": [1], null, true, {\"y\": 1}, error}"
argument_list|)
decl_stmt|;
name|t
operator|.
name|read
argument_list|(
literal|'{'
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"x"
argument_list|,
name|t
operator|.
name|readString
argument_list|()
argument_list|)
expr_stmt|;
name|t
operator|.
name|read
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"[1]"
argument_list|,
name|t
operator|.
name|readRawValue
argument_list|()
argument_list|)
expr_stmt|;
name|t
operator|.
name|read
argument_list|(
literal|','
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"null"
argument_list|,
name|t
operator|.
name|readRawValue
argument_list|()
argument_list|)
expr_stmt|;
name|t
operator|.
name|read
argument_list|(
literal|','
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"true"
argument_list|,
name|t
operator|.
name|readRawValue
argument_list|()
argument_list|)
expr_stmt|;
name|t
operator|.
name|read
argument_list|(
literal|','
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"{\"y\": 1}"
argument_list|,
name|t
operator|.
name|readRawValue
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|t
operator|.
name|readRawValue
argument_list|()
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
comment|// expected
block|}
block|}
specifier|public
name|void
name|testTokenizer
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|"test"
argument_list|,
name|JsopTokenizer
operator|.
name|decode
argument_list|(
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"test"
argument_list|,
name|JsopTokenizer
operator|.
name|decodeQuoted
argument_list|(
literal|"\"test\""
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"hello\n"
operator|+
literal|"world"
argument_list|,
name|JsopTokenizer
operator|.
name|decodeQuoted
argument_list|(
literal|"\"hello\\n"
operator|+
literal|"world\""
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|JsopTokenizer
operator|.
name|decodeQuoted
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
comment|// ok
block|}
try|try
block|{
name|JsopTokenizer
operator|.
name|decode
argument_list|(
literal|"test\\"
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
comment|// ok
block|}
try|try
block|{
name|JsopTokenizer
operator|.
name|decode
argument_list|(
literal|"wrong\\uxxxx"
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
comment|// ok
block|}
try|try
block|{
name|JsopTokenizer
operator|.
name|decode
argument_list|(
literal|"wrong\\m"
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
comment|// ok
block|}
name|test
argument_list|(
literal|"/error/"
argument_list|,
literal|"\"\\"
argument_list|)
expr_stmt|;
name|test
argument_list|(
literal|"/error/1"
argument_list|,
literal|".1"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"x"
argument_list|,
operator|new
name|JsopTokenizer
argument_list|(
literal|"x"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|test
argument_list|(
literal|"/id:truetrue/"
argument_list|,
literal|"true"
operator|+
literal|"true"
argument_list|)
expr_stmt|;
name|test
argument_list|(
literal|"/id:truer/"
argument_list|,
literal|"truer"
argument_list|)
expr_stmt|;
name|test
argument_list|(
literal|"/id:falsehood/"
argument_list|,
literal|"falsehood"
argument_list|)
expr_stmt|;
name|test
argument_list|(
literal|"/id:nil/"
argument_list|,
literal|"nil"
argument_list|)
expr_stmt|;
name|test
argument_list|(
literal|"/id:nil/1"
argument_list|,
literal|"nil 1"
argument_list|)
expr_stmt|;
name|test
argument_list|(
literal|"/error/"
argument_list|,
literal|"\"invalid"
argument_list|)
expr_stmt|;
name|test
argument_list|(
literal|"- \"test/test\""
argument_list|,
literal|"-\"test\\/test\""
argument_list|)
expr_stmt|;
name|test
argument_list|(
literal|" {\n\"x\": 1,\n\"y\": 2\n}\n"
argument_list|,
literal|"{\"x\":1, \"y\":2}"
argument_list|)
expr_stmt|;
name|test
argument_list|(
literal|"[true, false, null]"
argument_list|,
literal|"[true, false, null]"
argument_list|)
expr_stmt|;
name|test
argument_list|(
literal|"\"\""
argument_list|,
literal|"\"\""
argument_list|)
expr_stmt|;
name|test
argument_list|(
literal|"\"\\u0003\""
argument_list|,
literal|"\"\\u0003\""
argument_list|)
expr_stmt|;
name|test
argument_list|(
literal|"\"\\u0012\""
argument_list|,
literal|"\"\\u0012\""
argument_list|)
expr_stmt|;
name|test
argument_list|(
literal|"\"\\u0123\""
argument_list|,
literal|"\"\\u0123\""
argument_list|)
expr_stmt|;
name|test
argument_list|(
literal|"\"\\u1234\""
argument_list|,
literal|"\"\\u1234\""
argument_list|)
expr_stmt|;
name|test
argument_list|(
literal|"\"-\\\\-\\\"-\\b-\\f-\\n-\\r-\\t\""
argument_list|,
literal|"\"-\\\\-\\\"-\\b-\\f-\\n-\\r-\\t\""
argument_list|)
expr_stmt|;
name|test
argument_list|(
literal|"\"-\\b-\\f-\\n-\\r-\\t\""
argument_list|,
literal|"\"-\b-\f-\n-\r-\t\""
argument_list|)
expr_stmt|;
name|test
argument_list|(
literal|"[0, 12, -1, 0.1, -0.1, -2.3e1, 1e+1, 1.e-20]"
argument_list|,
literal|"[0,12,-1,0.1,-0.1,-2.3e1,1e+1,1.e-20]"
argument_list|)
expr_stmt|;
name|test
argument_list|(
literal|"\"Hello\""
argument_list|,
literal|"\"Hello\""
argument_list|)
expr_stmt|;
name|test
argument_list|(
literal|"[]"
argument_list|,
literal|"[]"
argument_list|)
expr_stmt|;
name|test
argument_list|(
literal|" {\n\n}\n"
argument_list|,
literal|"{}"
argument_list|)
expr_stmt|;
name|test
argument_list|(
literal|" {\n\"a\": /* test */ 10\n}\n"
argument_list|,
literal|"{ \"a\": /* test */ 10}"
argument_list|)
expr_stmt|;
name|test
argument_list|(
literal|"+ - / ^ "
argument_list|,
literal|"+ - / ^"
argument_list|)
expr_stmt|;
name|test
argument_list|(
literal|"/*/ comment /*/ "
argument_list|,
literal|"/*/ comment /*/"
argument_list|)
expr_stmt|;
name|test
argument_list|(
literal|"/**/ /id:comment//**/ "
argument_list|,
literal|"/**/ comment /**/"
argument_list|)
expr_stmt|;
name|JsopTokenizer
name|t
init|=
operator|new
name|JsopTokenizer
argument_list|(
literal|"{}123"
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|t
operator|.
name|matches
argument_list|(
literal|'+'
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|t
operator|.
name|matches
argument_list|(
literal|'{'
argument_list|)
argument_list|)
expr_stmt|;
name|t
operator|.
name|read
argument_list|(
literal|'}'
argument_list|)
expr_stmt|;
try|try
block|{
name|t
operator|.
name|read
argument_list|(
literal|'+'
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"{}123[*] expected: '+'"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|t
operator|.
name|read
argument_list|(
name|JsopTokenizer
operator|.
name|STRING
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"{}123[*] expected: string"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|static
name|void
name|test
parameter_list|(
name|String
name|expected
parameter_list|,
name|String
name|json
parameter_list|)
block|{
name|String
name|j2
init|=
name|prettyPrintWithErrors
argument_list|(
name|json
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|j2
argument_list|)
expr_stmt|;
block|}
specifier|static
name|String
name|prettyPrintWithErrors
parameter_list|(
name|String
name|jsop
parameter_list|)
block|{
name|StringBuilder
name|buff
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|JsopTokenizer
name|t
init|=
operator|new
name|JsopTokenizer
argument_list|(
name|jsop
argument_list|)
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|prettyPrint
argument_list|(
name|buff
argument_list|,
name|t
argument_list|,
literal|""
argument_list|)
expr_stmt|;
if|if
condition|(
name|t
operator|.
name|getTokenType
argument_list|()
operator|==
name|JsopTokenizer
operator|.
name|END
condition|)
block|{
return|return
name|buff
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
block|}
specifier|static
name|String
name|prettyPrint
parameter_list|(
name|StringBuilder
name|buff
parameter_list|,
name|JsopTokenizer
name|t
parameter_list|,
name|String
name|ident
parameter_list|)
block|{
name|String
name|space
init|=
literal|""
decl_stmt|;
name|boolean
name|inArray
init|=
literal|false
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
switch|switch
condition|(
name|t
operator|.
name|read
argument_list|()
condition|)
block|{
case|case
name|JsopTokenizer
operator|.
name|END
case|:
return|return
name|buff
operator|.
name|toString
argument_list|()
return|;
case|case
name|JsopTokenizer
operator|.
name|STRING
case|:
name|buff
operator|.
name|append
argument_list|(
name|JsopBuilder
operator|.
name|encode
argument_list|(
name|t
operator|.
name|getToken
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|JsopTokenizer
operator|.
name|NUMBER
case|:
name|buff
operator|.
name|append
argument_list|(
name|t
operator|.
name|getToken
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|JsopTokenizer
operator|.
name|TRUE
case|:
name|buff
operator|.
name|append
argument_list|(
literal|"true"
argument_list|)
expr_stmt|;
break|break;
case|case
name|JsopTokenizer
operator|.
name|FALSE
case|:
name|buff
operator|.
name|append
argument_list|(
literal|"false"
argument_list|)
expr_stmt|;
break|break;
case|case
name|JsopTokenizer
operator|.
name|NULL
case|:
name|buff
operator|.
name|append
argument_list|(
literal|"null"
argument_list|)
expr_stmt|;
break|break;
case|case
name|JsopTokenizer
operator|.
name|ERROR
case|:
name|buff
operator|.
name|append
argument_list|(
literal|"/error/"
argument_list|)
expr_stmt|;
break|break;
case|case
name|JsopTokenizer
operator|.
name|IDENTIFIER
case|:
name|buff
operator|.
name|append
argument_list|(
literal|"/id:"
argument_list|)
operator|.
name|append
argument_list|(
name|t
operator|.
name|getToken
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|'/'
argument_list|)
expr_stmt|;
break|break;
case|case
name|JsopTokenizer
operator|.
name|COMMENT
case|:
name|buff
operator|.
name|append
argument_list|(
literal|"/*"
argument_list|)
operator|.
name|append
argument_list|(
name|t
operator|.
name|getToken
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"*/ "
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'{'
case|:
name|buff
operator|.
name|append
argument_list|(
literal|" {\n"
argument_list|)
operator|.
name|append
argument_list|(
name|space
operator|+=
name|ident
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'}'
case|:
name|space
operator|=
name|space
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|space
operator|.
name|length
argument_list|()
operator|-
name|ident
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|buff
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
operator|.
name|append
argument_list|(
name|space
argument_list|)
operator|.
name|append
argument_list|(
literal|"}\n"
argument_list|)
operator|.
name|append
argument_list|(
name|space
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'['
case|:
name|inArray
operator|=
literal|true
expr_stmt|;
name|buff
operator|.
name|append
argument_list|(
literal|"["
argument_list|)
expr_stmt|;
break|break;
case|case
literal|']'
case|:
name|inArray
operator|=
literal|false
expr_stmt|;
name|buff
operator|.
name|append
argument_list|(
literal|"]"
argument_list|)
expr_stmt|;
break|break;
case|case
literal|','
case|:
if|if
condition|(
operator|!
name|inArray
condition|)
block|{
name|buff
operator|.
name|append
argument_list|(
literal|",\n"
argument_list|)
operator|.
name|append
argument_list|(
name|space
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|buff
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
block|}
break|break;
case|case
literal|':'
case|:
name|buff
operator|.
name|append
argument_list|(
literal|": "
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'+'
case|:
name|buff
operator|.
name|append
argument_list|(
literal|"+ "
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'-'
case|:
name|buff
operator|.
name|append
argument_list|(
literal|"- "
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'^'
case|:
name|buff
operator|.
name|append
argument_list|(
literal|"^ "
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'/'
case|:
name|buff
operator|.
name|append
argument_list|(
literal|"/ "
argument_list|)
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"token type: "
operator|+
name|t
operator|.
name|getTokenType
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
specifier|public
name|void
name|testBuilder
parameter_list|()
throws|throws
name|IOException
block|{
name|JsopBuilder
name|buff
init|=
operator|new
name|JsopBuilder
argument_list|()
decl_stmt|;
name|buff
operator|.
name|tag
argument_list|(
literal|'+'
argument_list|)
operator|.
name|object
argument_list|()
operator|.
name|key
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|value
argument_list|(
literal|"bar"
argument_list|)
operator|.
name|key
argument_list|(
literal|"int"
argument_list|)
operator|.
name|value
argument_list|(
literal|3
argument_list|)
operator|.
name|key
argument_list|(
literal|"decimal"
argument_list|)
operator|.
name|encodedValue
argument_list|(
literal|"3.0"
argument_list|)
operator|.
name|key
argument_list|(
literal|"obj"
argument_list|)
operator|.
name|object
argument_list|()
operator|.
name|key
argument_list|(
literal|"boolean"
argument_list|)
operator|.
name|value
argument_list|(
literal|true
argument_list|)
operator|.
name|key
argument_list|(
literal|"null"
argument_list|)
operator|.
name|value
argument_list|(
literal|null
argument_list|)
operator|.
name|key
argument_list|(
literal|"arr"
argument_list|)
operator|.
name|array
argument_list|()
operator|.
name|array
argument_list|()
operator|.
name|value
argument_list|(
literal|1
argument_list|)
operator|.
name|value
argument_list|(
literal|"\u001f ~ \u007f \u0080"
argument_list|)
operator|.
name|value
argument_list|(
literal|"42"
argument_list|)
operator|.
name|endArray
argument_list|()
operator|.
name|array
argument_list|()
operator|.
name|endArray
argument_list|()
operator|.
name|endArray
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|key
argument_list|(
literal|"some"
argument_list|)
operator|.
name|value
argument_list|(
literal|"more"
argument_list|)
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|String
name|json
init|=
name|buff
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"+{\"foo\":\"bar\",\"int\":3,\"decimal\":3.0,"
operator|+
literal|"\"obj\":{\"boolean\":true,\"null\":null,"
operator|+
literal|"\"arr\":[[1,\"\\u001f ~ \\u007f \\u0080\",\"42\"],[]]},\"some\":\"more\"}"
argument_list|,
name|json
argument_list|)
expr_stmt|;
name|buff
operator|.
name|resetWriter
argument_list|()
expr_stmt|;
name|buff
operator|.
name|array
argument_list|()
operator|.
name|object
argument_list|()
operator|.
name|key
argument_list|(
literal|"x"
argument_list|)
operator|.
name|value
argument_list|(
literal|"1"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|newline
argument_list|()
operator|.
name|object
argument_list|()
operator|.
name|key
argument_list|(
literal|"y"
argument_list|)
operator|.
name|value
argument_list|(
literal|"2"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|newline
argument_list|()
operator|.
name|endArray
argument_list|()
expr_stmt|;
name|json
operator|=
name|buff
operator|.
name|toString
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"[{\"x\":\"1\"}\n,{\"y\":\"2\"}\n]"
argument_list|,
name|json
argument_list|)
expr_stmt|;
name|buff
operator|=
operator|new
name|JsopBuilder
argument_list|()
expr_stmt|;
name|buff
operator|.
name|tag
argument_list|(
literal|'+'
argument_list|)
operator|.
name|key
argument_list|(
literal|"x"
argument_list|)
operator|.
name|value
argument_list|(
literal|"1"
argument_list|)
operator|.
name|newline
argument_list|()
expr_stmt|;
name|buff
operator|.
name|tag
argument_list|(
literal|'+'
argument_list|)
operator|.
name|key
argument_list|(
literal|"y"
argument_list|)
operator|.
name|value
argument_list|(
literal|"2"
argument_list|)
operator|.
name|newline
argument_list|()
expr_stmt|;
name|json
operator|=
name|buff
operator|.
name|toString
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"+\"x\":\"1\"\n+\"y\":\"2\"\n"
argument_list|,
name|json
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testEscape
parameter_list|()
throws|throws
name|IOException
block|{
name|assertEquals
argument_list|(
literal|"null"
argument_list|,
name|JsopBuilder
operator|.
name|encode
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|JsopBuilder
name|buff
init|=
operator|new
name|JsopBuilder
argument_list|()
operator|.
name|key
argument_list|(
literal|"back\\slash"
argument_list|)
operator|.
name|value
argument_list|(
literal|"\\"
argument_list|)
operator|.
name|key
argument_list|(
literal|"back\\\\slash"
argument_list|)
operator|.
name|value
argument_list|(
literal|"\\\\"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"\"back\\\\slash\":\"\\\\\",\"back\\\\\\\\slash\":\"\\\\\\\\\""
argument_list|,
name|buff
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testPrettyPrint
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|"{}"
argument_list|,
name|JsopBuilder
operator|.
name|prettyPrint
argument_list|(
literal|"{}"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"{\n  \"a\": 1,\n  \"b\": \"Hello\"\n}"
argument_list|,
name|JsopBuilder
operator|.
name|prettyPrint
argument_list|(
literal|"{\"a\":1,\"b\":\"Hello\"}"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"{\n  \"a\": [1, 2]\n}"
argument_list|,
name|JsopBuilder
operator|.
name|prettyPrint
argument_list|(
literal|"{\"a\":[1, 2]}"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|String
name|format
parameter_list|(
name|String
name|json
parameter_list|)
block|{
return|return
name|prettyPrint
argument_list|(
operator|new
name|StringBuilder
argument_list|()
argument_list|,
operator|new
name|JsopTokenizer
argument_list|(
name|json
argument_list|)
argument_list|,
literal|"    "
argument_list|)
return|;
block|}
block|}
end_class

end_unit

