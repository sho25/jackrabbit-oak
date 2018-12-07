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
name|spi
operator|.
name|security
operator|.
name|user
operator|.
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|NoSuchAlgorithmException
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
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableList
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
name|security
operator|.
name|ConfigurationParameters
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
name|security
operator|.
name|user
operator|.
name|UserConstants
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
name|util
operator|.
name|Text
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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertFalse
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|fail
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assume
operator|.
name|assumeFalse
import|;
end_import

begin_class
specifier|public
class|class
name|PasswordUtilTest
block|{
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|plainPasswords
decl_stmt|;
specifier|private
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|hashedPasswords
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|before
parameter_list|()
throws|throws
name|Exception
block|{
name|plainPasswords
operator|=
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"pw"
argument_list|,
literal|"PassWord123"
argument_list|,
literal|"_"
argument_list|,
literal|"{invalidAlgo}"
argument_list|,
literal|"{invalidAlgo}Password"
argument_list|,
literal|"{SHA-256}"
argument_list|,
literal|"pw{SHA-256}"
argument_list|,
literal|"p{SHA-256}w"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|hashedPasswords
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
for|for
control|(
name|String
name|pw
range|:
name|plainPasswords
control|)
block|{
name|hashedPasswords
operator|.
name|put
argument_list|(
name|pw
argument_list|,
name|PasswordUtil
operator|.
name|buildPasswordHash
argument_list|(
name|pw
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testBuildPasswordHash
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|String
name|pw
range|:
name|plainPasswords
control|)
block|{
name|String
name|pwHash
init|=
name|PasswordUtil
operator|.
name|buildPasswordHash
argument_list|(
name|pw
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|pw
operator|.
name|equals
argument_list|(
name|pwHash
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|Integer
index|[]
argument_list|>
name|l
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|l
operator|.
name|add
argument_list|(
operator|new
name|Integer
index|[]
block|{
literal|0
block|,
literal|1000
block|}
argument_list|)
expr_stmt|;
name|l
operator|.
name|add
argument_list|(
operator|new
name|Integer
index|[]
block|{
literal|1
block|,
literal|10
block|}
argument_list|)
expr_stmt|;
name|l
operator|.
name|add
argument_list|(
operator|new
name|Integer
index|[]
block|{
literal|8
block|,
literal|50
block|}
argument_list|)
expr_stmt|;
name|l
operator|.
name|add
argument_list|(
operator|new
name|Integer
index|[]
block|{
literal|10
block|,
literal|5
block|}
argument_list|)
expr_stmt|;
name|l
operator|.
name|add
argument_list|(
operator|new
name|Integer
index|[]
block|{
operator|-
literal|1
block|,
operator|-
literal|1
block|}
argument_list|)
expr_stmt|;
for|for
control|(
name|Integer
index|[]
name|params
range|:
name|l
control|)
block|{
for|for
control|(
name|String
name|pw
range|:
name|plainPasswords
control|)
block|{
name|int
name|saltsize
init|=
name|params
index|[
literal|0
index|]
decl_stmt|;
name|int
name|iterations
init|=
name|params
index|[
literal|1
index|]
decl_stmt|;
name|String
name|pwHash
init|=
name|PasswordUtil
operator|.
name|buildPasswordHash
argument_list|(
name|pw
argument_list|,
name|PasswordUtil
operator|.
name|DEFAULT_ALGORITHM
argument_list|,
name|saltsize
argument_list|,
name|iterations
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|pw
operator|.
name|equals
argument_list|(
name|pwHash
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testBuildPasswordHashInvalidAlgorithm
parameter_list|()
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|String
argument_list|>
name|invalidAlgorithms
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|invalidAlgorithms
operator|.
name|add
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|invalidAlgorithms
operator|.
name|add
argument_list|(
literal|"+"
argument_list|)
expr_stmt|;
name|invalidAlgorithms
operator|.
name|add
argument_list|(
literal|"invalid"
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|invalid
range|:
name|invalidAlgorithms
control|)
block|{
try|try
block|{
name|PasswordUtil
operator|.
name|buildPasswordHash
argument_list|(
literal|"pw"
argument_list|,
name|invalid
argument_list|,
name|PasswordUtil
operator|.
name|DEFAULT_SALT_SIZE
argument_list|,
name|PasswordUtil
operator|.
name|DEFAULT_ITERATIONS
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Invalid algorithm "
operator|+
name|invalid
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoSuchAlgorithmException
name|e
parameter_list|)
block|{
comment|// success
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testBuildPasswordHashNoIterations
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|hash
init|=
name|PasswordUtil
operator|.
name|buildPasswordHash
argument_list|(
literal|"pw"
argument_list|,
name|PasswordUtil
operator|.
name|DEFAULT_ALGORITHM
argument_list|,
name|PasswordUtil
operator|.
name|DEFAULT_SALT_SIZE
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|PasswordUtil
operator|.
name|isSame
argument_list|(
name|hash
argument_list|,
literal|"pw"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testBuildPasswordHashNoSalt
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|hash
init|=
name|PasswordUtil
operator|.
name|buildPasswordHash
argument_list|(
literal|"pw"
argument_list|,
name|PasswordUtil
operator|.
name|DEFAULT_ALGORITHM
argument_list|,
literal|0
argument_list|,
name|PasswordUtil
operator|.
name|DEFAULT_ITERATIONS
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|PasswordUtil
operator|.
name|isSame
argument_list|(
name|hash
argument_list|,
literal|"pw"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testBuildPasswordHashNoSaltNoIterations
parameter_list|()
throws|throws
name|Exception
block|{
name|assumeFalse
argument_list|(
name|PasswordUtil
operator|.
name|DEFAULT_ALGORITHM
operator|.
name|startsWith
argument_list|(
name|PasswordUtil
operator|.
name|PBKDF2_PREFIX
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|jr2Hash
init|=
literal|"{"
operator|+
name|PasswordUtil
operator|.
name|DEFAULT_ALGORITHM
operator|+
literal|"}"
operator|+
name|Text
operator|.
name|digest
argument_list|(
name|PasswordUtil
operator|.
name|DEFAULT_ALGORITHM
argument_list|,
literal|"pw"
operator|.
name|getBytes
argument_list|(
literal|"utf-8"
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|PasswordUtil
operator|.
name|isSame
argument_list|(
name|jr2Hash
argument_list|,
literal|"pw"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testBuildPasswordWithConfig
parameter_list|()
throws|throws
name|Exception
block|{
name|ConfigurationParameters
name|params
init|=
name|ConfigurationParameters
operator|.
name|of
argument_list|(
name|UserConstants
operator|.
name|PARAM_PASSWORD_SALT_SIZE
argument_list|,
literal|13
argument_list|,
name|UserConstants
operator|.
name|PARAM_PASSWORD_HASH_ITERATIONS
argument_list|,
literal|13
argument_list|)
decl_stmt|;
name|String
name|hash
init|=
name|PasswordUtil
operator|.
name|buildPasswordHash
argument_list|(
literal|"pw"
argument_list|,
name|params
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|PasswordUtil
operator|.
name|isSame
argument_list|(
name|hash
argument_list|,
literal|"pw"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testIsPlainTextPassword
parameter_list|()
block|{
for|for
control|(
name|String
name|pw
range|:
name|plainPasswords
control|)
block|{
name|assertTrue
argument_list|(
name|pw
operator|+
literal|" should be plain text."
argument_list|,
name|PasswordUtil
operator|.
name|isPlainTextPassword
argument_list|(
name|pw
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testIsPlainTextForNull
parameter_list|()
block|{
name|assertTrue
argument_list|(
name|PasswordUtil
operator|.
name|isPlainTextPassword
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testIsPlainTextForPwHash
parameter_list|()
block|{
for|for
control|(
name|String
name|pwHash
range|:
name|hashedPasswords
operator|.
name|values
argument_list|()
control|)
block|{
name|assertFalse
argument_list|(
name|pwHash
operator|+
literal|" should not be plain text."
argument_list|,
name|PasswordUtil
operator|.
name|isPlainTextPassword
argument_list|(
name|pwHash
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testIsSame
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|String
name|pw
range|:
name|hashedPasswords
operator|.
name|keySet
argument_list|()
control|)
block|{
name|String
name|pwHash
init|=
name|hashedPasswords
operator|.
name|get
argument_list|(
name|pw
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Not the same "
operator|+
name|pw
operator|+
literal|", "
operator|+
name|pwHash
argument_list|,
name|PasswordUtil
operator|.
name|isSame
argument_list|(
name|pwHash
argument_list|,
name|pw
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|String
name|pw
init|=
literal|"password"
decl_stmt|;
name|String
name|pwHash
init|=
name|PasswordUtil
operator|.
name|buildPasswordHash
argument_list|(
name|pw
argument_list|,
literal|"SHA-1"
argument_list|,
literal|4
argument_list|,
literal|50
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Not the same '"
operator|+
name|pw
operator|+
literal|"', "
operator|+
name|pwHash
argument_list|,
name|PasswordUtil
operator|.
name|isSame
argument_list|(
name|pwHash
argument_list|,
name|pw
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Not the same '"
operator|+
name|pw
operator|+
literal|"', "
operator|+
name|pwHash
argument_list|,
name|PasswordUtil
operator|.
name|isSame
argument_list|(
name|pwHash
argument_list|,
name|pw
operator|.
name|toCharArray
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|pwHash
operator|=
name|PasswordUtil
operator|.
name|buildPasswordHash
argument_list|(
name|pw
argument_list|,
literal|"md5"
argument_list|,
literal|0
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Not the same '"
operator|+
name|pw
operator|+
literal|"', "
operator|+
name|pwHash
argument_list|,
name|PasswordUtil
operator|.
name|isSame
argument_list|(
name|pwHash
argument_list|,
name|pw
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Not the same '"
operator|+
name|pw
operator|+
literal|"', "
operator|+
name|pwHash
argument_list|,
name|PasswordUtil
operator|.
name|isSame
argument_list|(
name|pwHash
argument_list|,
name|pw
operator|.
name|toCharArray
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|pwHash
operator|=
name|PasswordUtil
operator|.
name|buildPasswordHash
argument_list|(
name|pw
argument_list|,
literal|"md5"
argument_list|,
operator|-
literal|1
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Not the same '"
operator|+
name|pw
operator|+
literal|"', "
operator|+
name|pwHash
argument_list|,
name|PasswordUtil
operator|.
name|isSame
argument_list|(
name|pwHash
argument_list|,
name|pw
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Not the same '"
operator|+
name|pw
operator|+
literal|"', "
operator|+
name|pwHash
argument_list|,
name|PasswordUtil
operator|.
name|isSame
argument_list|(
name|pwHash
argument_list|,
name|pw
operator|.
name|toCharArray
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testIsNotSame
parameter_list|()
block|{
name|String
name|previous
init|=
literal|null
decl_stmt|;
for|for
control|(
name|String
name|pw
range|:
name|hashedPasswords
operator|.
name|keySet
argument_list|()
control|)
block|{
name|String
name|pwHash
init|=
name|hashedPasswords
operator|.
name|get
argument_list|(
name|pw
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|pw
argument_list|,
name|PasswordUtil
operator|.
name|isSame
argument_list|(
name|pw
argument_list|,
name|pw
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|pwHash
argument_list|,
name|PasswordUtil
operator|.
name|isSame
argument_list|(
name|pwHash
argument_list|,
name|pwHash
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|previous
operator|!=
literal|null
condition|)
block|{
name|assertFalse
argument_list|(
name|previous
argument_list|,
name|PasswordUtil
operator|.
name|isSame
argument_list|(
name|pwHash
argument_list|,
name|previous
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|previous
operator|=
name|pw
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testIsSameNoSuchAlgorithmException
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|hash
init|=
name|PasswordUtil
operator|.
name|buildPasswordHash
argument_list|(
literal|"pw"
argument_list|)
decl_stmt|;
name|String
name|invalid
init|=
literal|"{invalidAlgorithm}"
operator|+
name|hash
operator|.
name|substring
argument_list|(
name|hash
operator|.
name|indexOf
argument_list|(
literal|'}'
argument_list|)
operator|+
literal|1
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|PasswordUtil
operator|.
name|isSame
argument_list|(
name|invalid
argument_list|,
literal|"pw"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testIsSameNullHash
parameter_list|()
block|{
name|assertFalse
argument_list|(
name|PasswordUtil
operator|.
name|isSame
argument_list|(
literal|null
argument_list|,
literal|"pw"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testIsSameNullPw
parameter_list|()
throws|throws
name|Exception
block|{
name|assertFalse
argument_list|(
name|PasswordUtil
operator|.
name|isSame
argument_list|(
name|PasswordUtil
operator|.
name|buildPasswordHash
argument_list|(
literal|"pw"
argument_list|)
argument_list|,
operator|(
name|String
operator|)
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testIsSameEmpty
parameter_list|()
throws|throws
name|Exception
block|{
name|assertTrue
argument_list|(
name|PasswordUtil
operator|.
name|isSame
argument_list|(
name|PasswordUtil
operator|.
name|buildPasswordHash
argument_list|(
literal|""
argument_list|)
argument_list|,
literal|""
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testIsSameEmptyHash
parameter_list|()
block|{
name|assertFalse
argument_list|(
name|PasswordUtil
operator|.
name|isSame
argument_list|(
literal|""
argument_list|,
literal|"pw"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testIsSameEmptyPw
parameter_list|()
throws|throws
name|Exception
block|{
name|assertFalse
argument_list|(
name|PasswordUtil
operator|.
name|isSame
argument_list|(
name|PasswordUtil
operator|.
name|buildPasswordHash
argument_list|(
literal|"pw"
argument_list|)
argument_list|,
literal|""
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPBKDF2With
parameter_list|()
throws|throws
name|Exception
block|{
comment|// https://docs.oracle.com/javase/8/docs/technotes/guides/security/StandardNames.html
name|String
name|algo
init|=
literal|"PBKDF2WithHmacSHA512"
decl_stmt|;
comment|// test vector from http://tools.ietf.org/html/rfc6070
name|String
name|pw
init|=
literal|"pass\0word"
decl_stmt|;
name|int
name|iterations
init|=
literal|4096
decl_stmt|;
name|String
name|hash
init|=
name|PasswordUtil
operator|.
name|buildPasswordHash
argument_list|(
name|pw
argument_list|,
name|algo
argument_list|,
literal|5
argument_list|,
name|iterations
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|hash
operator|.
name|startsWith
argument_list|(
literal|"{"
operator|+
name|algo
operator|+
literal|"}"
argument_list|)
argument_list|)
expr_stmt|;
name|int
name|cntOctets
init|=
name|hash
operator|.
name|substring
argument_list|(
name|hash
operator|.
name|lastIndexOf
argument_list|(
literal|'-'
argument_list|)
operator|+
literal|1
argument_list|)
operator|.
name|length
argument_list|()
operator|/
literal|2
decl_stmt|;
name|assertEquals
argument_list|(
literal|16
argument_list|,
name|cntOctets
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|PasswordUtil
operator|.
name|isPlainTextPassword
argument_list|(
name|hash
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|PasswordUtil
operator|.
name|isSame
argument_list|(
name|hash
argument_list|,
name|pw
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

