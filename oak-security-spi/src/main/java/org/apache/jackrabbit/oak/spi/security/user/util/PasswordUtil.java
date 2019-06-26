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
name|io
operator|.
name|UnsupportedEncodingException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|Key
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|MessageDigest
import|;
end_import

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
name|security
operator|.
name|SecureRandom
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|spec
operator|.
name|InvalidKeySpecException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|spec
operator|.
name|KeySpec
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|crypto
operator|.
name|SecretKeyFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|crypto
operator|.
name|spec
operator|.
name|PBEKeySpec
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
name|base
operator|.
name|Strings
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
name|jetbrains
operator|.
name|annotations
operator|.
name|NotNull
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|Nullable
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
operator|.
name|checkNotNull
import|;
end_import

begin_comment
comment|/**  * Utility to generate and compare password hashes.  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|PasswordUtil
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|PasswordUtil
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|char
name|DELIMITER
init|=
literal|'-'
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|NO_ITERATIONS
init|=
literal|1
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|ENCODING
init|=
literal|"UTF-8"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|PBKDF2_KEY_LENGTH
init|=
literal|128
decl_stmt|;
comment|/**      * @since OAK 1.0      */
specifier|static
specifier|final
name|String
name|PBKDF2_PREFIX
init|=
literal|"PBKDF2"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_ALGORITHM
init|=
literal|"SHA-256"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_SALT_SIZE
init|=
literal|8
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_ITERATIONS
init|=
literal|1000
decl_stmt|;
comment|/**      * Avoid instantiation      */
specifier|private
name|PasswordUtil
parameter_list|()
block|{}
comment|/**      * Generates a hash of the specified password with the default values      * for algorithm, salt-size and number of iterations.      *      * @param password The password to be hashed.      * @return The password hash.      * @throws NoSuchAlgorithmException If {@link #DEFAULT_ALGORITHM} is not supported.      * @throws UnsupportedEncodingException If utf-8 is not supported.      */
specifier|public
specifier|static
name|String
name|buildPasswordHash
parameter_list|(
annotation|@
name|NotNull
name|String
name|password
parameter_list|)
throws|throws
name|NoSuchAlgorithmException
throws|,
name|UnsupportedEncodingException
block|{
return|return
name|buildPasswordHash
argument_list|(
name|password
argument_list|,
name|DEFAULT_ALGORITHM
argument_list|,
name|DEFAULT_SALT_SIZE
argument_list|,
name|DEFAULT_ITERATIONS
argument_list|)
return|;
block|}
comment|/**      * Generates a hash of the specified password using the specified algorithm,      * salt size and number of iterations into account.      *      * @param password The password to be hashed.      * @param algorithm The desired hash algorithm. If the algorith is      * {@code null} the {@link #DEFAULT_ALGORITHM} will be used.      * @param saltSize The desired salt size. If the specified integer is lower      * that {@link #DEFAULT_SALT_SIZE} the default is used.      * @param iterations The desired number of iterations. If the specified      * integer is lower than 1 the {@link #DEFAULT_ITERATIONS default} value is used.      * @return  The password hash.      * @throws NoSuchAlgorithmException If the specified algorithm is not supported.      * @throws UnsupportedEncodingException If utf-8 is not supported.      */
specifier|public
specifier|static
name|String
name|buildPasswordHash
parameter_list|(
annotation|@
name|NotNull
name|String
name|password
parameter_list|,
annotation|@
name|Nullable
name|String
name|algorithm
parameter_list|,
name|int
name|saltSize
parameter_list|,
name|int
name|iterations
parameter_list|)
throws|throws
name|NoSuchAlgorithmException
throws|,
name|UnsupportedEncodingException
block|{
name|checkNotNull
argument_list|(
name|password
argument_list|)
expr_stmt|;
if|if
condition|(
name|iterations
operator|<
name|NO_ITERATIONS
condition|)
block|{
name|iterations
operator|=
name|DEFAULT_ITERATIONS
expr_stmt|;
block|}
if|if
condition|(
name|saltSize
operator|<
name|DEFAULT_SALT_SIZE
condition|)
block|{
name|saltSize
operator|=
name|DEFAULT_SALT_SIZE
expr_stmt|;
block|}
name|String
name|salt
init|=
name|generateSalt
argument_list|(
name|saltSize
argument_list|)
decl_stmt|;
name|String
name|alg
init|=
operator|(
name|algorithm
operator|==
literal|null
operator|)
condition|?
name|DEFAULT_ALGORITHM
else|:
name|algorithm
decl_stmt|;
return|return
name|generateHash
argument_list|(
name|password
argument_list|,
name|alg
argument_list|,
name|salt
argument_list|,
name|iterations
argument_list|)
return|;
block|}
comment|/**      * Same as {@link #buildPasswordHash(String, String, int, int)} but retrieving      * the parameters for hash generation from the specified configuration.      *      * @param password The password to be hashed.      * @param config The configuration defining the details of the hash generation.      * @return The password hash.      * @throws NoSuchAlgorithmException If the specified algorithm is not supported.      * @throws UnsupportedEncodingException If utf-8 is not supported.      */
specifier|public
specifier|static
name|String
name|buildPasswordHash
parameter_list|(
annotation|@
name|NotNull
name|String
name|password
parameter_list|,
annotation|@
name|NotNull
name|ConfigurationParameters
name|config
parameter_list|)
throws|throws
name|NoSuchAlgorithmException
throws|,
name|UnsupportedEncodingException
block|{
name|checkNotNull
argument_list|(
name|config
argument_list|)
expr_stmt|;
name|String
name|algorithm
init|=
name|config
operator|.
name|getConfigValue
argument_list|(
name|UserConstants
operator|.
name|PARAM_PASSWORD_HASH_ALGORITHM
argument_list|,
name|DEFAULT_ALGORITHM
argument_list|)
decl_stmt|;
name|int
name|iterations
init|=
name|config
operator|.
name|getConfigValue
argument_list|(
name|UserConstants
operator|.
name|PARAM_PASSWORD_HASH_ITERATIONS
argument_list|,
name|DEFAULT_ITERATIONS
argument_list|)
decl_stmt|;
name|int
name|saltSize
init|=
name|config
operator|.
name|getConfigValue
argument_list|(
name|UserConstants
operator|.
name|PARAM_PASSWORD_SALT_SIZE
argument_list|,
name|DEFAULT_SALT_SIZE
argument_list|)
decl_stmt|;
return|return
name|buildPasswordHash
argument_list|(
name|password
argument_list|,
name|algorithm
argument_list|,
name|saltSize
argument_list|,
name|iterations
argument_list|)
return|;
block|}
comment|/**      * Returns {@code true} if the specified string doesn't start with a      * valid algorithm name in curly brackets.      *      * @param password The string to be tested.      * @return {@code true} if the specified string doesn't start with a      * valid algorithm name in curly brackets.      */
specifier|public
specifier|static
name|boolean
name|isPlainTextPassword
parameter_list|(
annotation|@
name|Nullable
name|String
name|password
parameter_list|)
block|{
return|return
name|extractAlgorithm
argument_list|(
name|password
argument_list|)
operator|==
literal|null
return|;
block|}
comment|/**      * Returns {@code true} if hash of the specified {@code password} equals the      * given hashed password.      *      * @param hashedPassword Password hash.      * @param password The password to compare.      * @return If the hash created from the specified {@code password} equals      * the given {@code hashedPassword} string.      */
specifier|public
specifier|static
name|boolean
name|isSame
parameter_list|(
annotation|@
name|Nullable
name|String
name|hashedPassword
parameter_list|,
annotation|@
name|NotNull
name|char
index|[]
name|password
parameter_list|)
block|{
return|return
name|isSame
argument_list|(
name|hashedPassword
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|password
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * Returns {@code true} if hash of the specified {@code password} equals the      * given hashed password.      *      * @param hashedPassword Password hash.      * @param password The password to compare.      * @return If the hash created from the specified {@code password} equals      * the given {@code hashedPassword} string.      */
specifier|public
specifier|static
name|boolean
name|isSame
parameter_list|(
annotation|@
name|Nullable
name|String
name|hashedPassword
parameter_list|,
annotation|@
name|NotNull
name|String
name|password
parameter_list|)
block|{
if|if
condition|(
name|hashedPassword
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
try|try
block|{
name|String
name|algorithm
init|=
name|extractAlgorithm
argument_list|(
name|hashedPassword
argument_list|)
decl_stmt|;
if|if
condition|(
name|algorithm
operator|!=
literal|null
condition|)
block|{
name|int
name|startPos
init|=
name|algorithm
operator|.
name|length
argument_list|()
operator|+
literal|2
decl_stmt|;
name|String
name|salt
init|=
name|extractSalt
argument_list|(
name|hashedPassword
argument_list|,
name|startPos
argument_list|)
decl_stmt|;
name|int
name|iterations
init|=
name|NO_ITERATIONS
decl_stmt|;
if|if
condition|(
name|salt
operator|!=
literal|null
condition|)
block|{
name|startPos
operator|+=
name|salt
operator|.
name|length
argument_list|()
operator|+
literal|1
expr_stmt|;
name|iterations
operator|=
name|extractIterations
argument_list|(
name|hashedPassword
argument_list|,
name|startPos
argument_list|)
expr_stmt|;
block|}
name|String
name|hash
init|=
name|generateHash
argument_list|(
name|password
argument_list|,
name|algorithm
argument_list|,
name|salt
argument_list|,
name|iterations
argument_list|)
decl_stmt|;
return|return
name|compareSecure
argument_list|(
name|hashedPassword
argument_list|,
name|hash
argument_list|)
return|;
block|}
comment|// hashedPassword is plaintext -> return false
block|}
catch|catch
parameter_list|(
name|NoSuchAlgorithmException
decl||
name|UnsupportedEncodingException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
literal|false
return|;
block|}
comment|//------------------------------------------------------------< private>---
comment|/**      * Compare two strings. The comparison is constant time: it will always loop      * over all characters and doesn't use conditional operations in the loop to      * make sure an attacker can not use a timing attack.      *      * @param a      * @param b      * @return true if both parameters contain the same data.      */
specifier|private
specifier|static
name|boolean
name|compareSecure
parameter_list|(
annotation|@
name|NotNull
name|String
name|a
parameter_list|,
annotation|@
name|NotNull
name|String
name|b
parameter_list|)
block|{
name|int
name|len
init|=
name|a
operator|.
name|length
argument_list|()
decl_stmt|;
if|if
condition|(
name|len
operator|!=
name|b
operator|.
name|length
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|len
operator|==
literal|0
condition|)
block|{
return|return
literal|true
return|;
block|}
comment|// don't use conditional operations inside the loop
name|int
name|bits
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
name|len
condition|;
name|i
operator|++
control|)
block|{
comment|// this will never reset any bits
name|bits
operator||=
name|a
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
operator|^
name|b
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
return|return
name|bits
operator|==
literal|0
return|;
block|}
annotation|@
name|NotNull
specifier|private
specifier|static
name|String
name|generateHash
parameter_list|(
annotation|@
name|NotNull
name|String
name|pwd
parameter_list|,
annotation|@
name|NotNull
name|String
name|algorithm
parameter_list|,
annotation|@
name|Nullable
name|String
name|salt
parameter_list|,
name|int
name|iterations
parameter_list|)
throws|throws
name|NoSuchAlgorithmException
throws|,
name|UnsupportedEncodingException
block|{
name|StringBuilder
name|passwordHash
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|passwordHash
operator|.
name|append
argument_list|(
literal|'{'
argument_list|)
operator|.
name|append
argument_list|(
name|algorithm
argument_list|)
operator|.
name|append
argument_list|(
literal|'}'
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|salt
argument_list|)
condition|)
block|{
name|StringBuilder
name|data
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|data
operator|.
name|append
argument_list|(
name|salt
argument_list|)
operator|.
name|append
argument_list|(
name|pwd
argument_list|)
expr_stmt|;
name|passwordHash
operator|.
name|append
argument_list|(
name|salt
argument_list|)
operator|.
name|append
argument_list|(
name|DELIMITER
argument_list|)
expr_stmt|;
if|if
condition|(
name|iterations
operator|>
name|NO_ITERATIONS
condition|)
block|{
name|passwordHash
operator|.
name|append
argument_list|(
name|iterations
argument_list|)
operator|.
name|append
argument_list|(
name|DELIMITER
argument_list|)
expr_stmt|;
block|}
name|String
name|digest
decl_stmt|;
if|if
condition|(
name|algorithm
operator|.
name|startsWith
argument_list|(
name|PBKDF2_PREFIX
argument_list|)
condition|)
block|{
name|digest
operator|=
name|generatePBKDF2
argument_list|(
name|pwd
argument_list|,
name|salt
argument_list|,
name|algorithm
argument_list|,
name|iterations
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|digest
operator|=
name|generateDigest
argument_list|(
name|data
operator|.
name|toString
argument_list|()
argument_list|,
name|algorithm
argument_list|,
name|iterations
argument_list|)
expr_stmt|;
block|}
name|passwordHash
operator|.
name|append
argument_list|(
name|digest
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// backwards compatible to jr 2.0: no salt, no iterations
name|passwordHash
operator|.
name|append
argument_list|(
name|Text
operator|.
name|digest
argument_list|(
name|algorithm
argument_list|,
name|pwd
operator|.
name|getBytes
argument_list|(
name|ENCODING
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|passwordHash
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|NotNull
specifier|private
specifier|static
name|String
name|generateSalt
parameter_list|(
name|int
name|saltSize
parameter_list|)
block|{
name|SecureRandom
name|random
init|=
operator|new
name|SecureRandom
argument_list|()
decl_stmt|;
name|byte
index|[]
name|salt
init|=
operator|new
name|byte
index|[
name|saltSize
index|]
decl_stmt|;
name|random
operator|.
name|nextBytes
argument_list|(
name|salt
argument_list|)
expr_stmt|;
return|return
name|convertBytesToHex
argument_list|(
name|salt
argument_list|)
return|;
block|}
comment|/**      * Convert a byte array to a hex encoded string.      *      * @param bytes the byte array      * @return the hex encoded string      */
annotation|@
name|NotNull
specifier|private
specifier|static
name|String
name|convertBytesToHex
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|)
block|{
name|StringBuilder
name|res
init|=
operator|new
name|StringBuilder
argument_list|(
name|bytes
operator|.
name|length
operator|*
literal|2
argument_list|)
decl_stmt|;
for|for
control|(
name|byte
name|b
range|:
name|bytes
control|)
block|{
name|res
operator|.
name|append
argument_list|(
name|Text
operator|.
name|hexTable
index|[
operator|(
name|b
operator|>>
literal|4
operator|)
operator|&
literal|15
index|]
argument_list|)
expr_stmt|;
name|res
operator|.
name|append
argument_list|(
name|Text
operator|.
name|hexTable
index|[
name|b
operator|&
literal|15
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|res
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**      * Convert a hex encoded string to a byte array.      *      * @param s the hex encoded string      * @return the byte array      */
annotation|@
name|NotNull
specifier|private
specifier|static
name|byte
index|[]
name|convertHexToBytes
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|int
name|len
init|=
name|s
operator|.
name|length
argument_list|()
decl_stmt|;
if|if
condition|(
name|len
operator|%
literal|2
operator|!=
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Not a hex encoded byte array: "
operator|+
name|s
argument_list|)
throw|;
block|}
name|byte
index|[]
name|bytes
init|=
operator|new
name|byte
index|[
name|len
operator|/
literal|2
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
name|bytes
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|bytes
index|[
name|i
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
operator|(
name|Character
operator|.
name|digit
argument_list|(
name|s
operator|.
name|charAt
argument_list|(
name|i
operator|+
name|i
argument_list|)
argument_list|,
literal|16
argument_list|)
operator|<<
literal|4
operator|)
operator|+
name|Character
operator|.
name|digit
argument_list|(
name|s
operator|.
name|charAt
argument_list|(
name|i
operator|+
name|i
operator|+
literal|1
argument_list|)
argument_list|,
literal|16
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|bytes
return|;
block|}
annotation|@
name|NotNull
specifier|private
specifier|static
name|String
name|generatePBKDF2
parameter_list|(
annotation|@
name|NotNull
name|String
name|pwd
parameter_list|,
annotation|@
name|NotNull
name|String
name|salt
parameter_list|,
annotation|@
name|NotNull
name|String
name|algorithm
parameter_list|,
name|int
name|iterations
parameter_list|)
throws|throws
name|NoSuchAlgorithmException
block|{
comment|// for example PBKDF2WithHmacSHA1
name|SecretKeyFactory
name|factory
init|=
name|SecretKeyFactory
operator|.
name|getInstance
argument_list|(
name|algorithm
argument_list|)
decl_stmt|;
name|byte
index|[]
name|saltBytes
init|=
name|convertHexToBytes
argument_list|(
name|salt
argument_list|)
decl_stmt|;
name|KeySpec
name|keyspec
init|=
operator|new
name|PBEKeySpec
argument_list|(
name|pwd
operator|.
name|toCharArray
argument_list|()
argument_list|,
name|saltBytes
argument_list|,
name|iterations
argument_list|,
name|PBKDF2_KEY_LENGTH
argument_list|)
decl_stmt|;
try|try
block|{
name|Key
name|key
init|=
name|factory
operator|.
name|generateSecret
argument_list|(
name|keyspec
argument_list|)
decl_stmt|;
name|byte
index|[]
name|bytes
init|=
name|key
operator|.
name|getEncoded
argument_list|()
decl_stmt|;
return|return
name|convertBytesToHex
argument_list|(
name|bytes
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|InvalidKeySpecException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|NoSuchAlgorithmException
argument_list|(
name|algorithm
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|NotNull
specifier|private
specifier|static
name|String
name|generateDigest
parameter_list|(
annotation|@
name|NotNull
name|String
name|data
parameter_list|,
annotation|@
name|NotNull
name|String
name|algorithm
parameter_list|,
name|int
name|iterations
parameter_list|)
throws|throws
name|UnsupportedEncodingException
throws|,
name|NoSuchAlgorithmException
block|{
name|byte
index|[]
name|bytes
init|=
name|data
operator|.
name|getBytes
argument_list|(
name|ENCODING
argument_list|)
decl_stmt|;
name|MessageDigest
name|md
init|=
name|MessageDigest
operator|.
name|getInstance
argument_list|(
name|algorithm
argument_list|)
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
name|iterations
condition|;
name|i
operator|++
control|)
block|{
name|md
operator|.
name|reset
argument_list|()
expr_stmt|;
name|bytes
operator|=
name|md
operator|.
name|digest
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
block|}
return|return
name|convertBytesToHex
argument_list|(
name|bytes
argument_list|)
return|;
block|}
comment|/**      * Extract the algorithm from the given crypted password string. Returns the      * algorithm or {@code null} if the given string doesn't have a      * leading {@code algorithm} such as created by {@code buildPasswordHash}      * or if the extracted string doesn't represent an available algorithm.      *      * @param hashedPwd The password hash.      * @return The algorithm or {@code null} if the given string doesn't have a      * leading {@code algorithm} such as created by {@code buildPasswordHash}      * or if the extracted string isn't a supported algorithm.      */
annotation|@
name|Nullable
specifier|private
specifier|static
name|String
name|extractAlgorithm
parameter_list|(
annotation|@
name|Nullable
name|String
name|hashedPwd
parameter_list|)
block|{
if|if
condition|(
operator|!
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|hashedPwd
argument_list|)
condition|)
block|{
name|int
name|end
init|=
name|hashedPwd
operator|.
name|indexOf
argument_list|(
literal|'}'
argument_list|)
decl_stmt|;
if|if
condition|(
name|hashedPwd
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|==
literal|'{'
operator|&&
name|end
operator|>
literal|0
operator|&&
name|end
operator|<
name|hashedPwd
operator|.
name|length
argument_list|()
operator|-
literal|1
condition|)
block|{
name|String
name|algorithm
init|=
name|hashedPwd
operator|.
name|substring
argument_list|(
literal|1
argument_list|,
name|end
argument_list|)
decl_stmt|;
try|try
block|{
if|if
condition|(
name|algorithm
operator|.
name|startsWith
argument_list|(
name|PBKDF2_PREFIX
argument_list|)
condition|)
block|{
name|SecretKeyFactory
operator|.
name|getInstance
argument_list|(
name|algorithm
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|MessageDigest
operator|.
name|getInstance
argument_list|(
name|algorithm
argument_list|)
expr_stmt|;
block|}
return|return
name|algorithm
return|;
block|}
catch|catch
parameter_list|(
name|NoSuchAlgorithmException
name|e
parameter_list|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Invalid algorithm detected {}"
argument_list|,
name|algorithm
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// not starting with {} or invalid algorithm
return|return
literal|null
return|;
block|}
annotation|@
name|Nullable
specifier|private
specifier|static
name|String
name|extractSalt
parameter_list|(
annotation|@
name|NotNull
name|String
name|hashedPwd
parameter_list|,
name|int
name|start
parameter_list|)
block|{
name|int
name|end
init|=
name|hashedPwd
operator|.
name|indexOf
argument_list|(
name|DELIMITER
argument_list|,
name|start
argument_list|)
decl_stmt|;
if|if
condition|(
name|end
operator|>
operator|-
literal|1
condition|)
block|{
return|return
name|hashedPwd
operator|.
name|substring
argument_list|(
name|start
argument_list|,
name|end
argument_list|)
return|;
block|}
else|else
block|{
comment|// no salt
return|return
literal|null
return|;
block|}
block|}
specifier|private
specifier|static
name|int
name|extractIterations
parameter_list|(
annotation|@
name|NotNull
name|String
name|hashedPwd
parameter_list|,
name|int
name|start
parameter_list|)
block|{
name|int
name|end
init|=
name|hashedPwd
operator|.
name|indexOf
argument_list|(
name|DELIMITER
argument_list|,
name|start
argument_list|)
decl_stmt|;
if|if
condition|(
name|end
operator|>
operator|-
literal|1
condition|)
block|{
name|String
name|str
init|=
name|hashedPwd
operator|.
name|substring
argument_list|(
name|start
argument_list|,
name|end
argument_list|)
decl_stmt|;
try|try
block|{
return|return
name|Integer
operator|.
name|parseInt
argument_list|(
name|str
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Expected number of iterations. Found: {}"
argument_list|,
name|str
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
comment|// no extra iterations
return|return
name|NO_ITERATIONS
return|;
block|}
block|}
end_class

end_unit

