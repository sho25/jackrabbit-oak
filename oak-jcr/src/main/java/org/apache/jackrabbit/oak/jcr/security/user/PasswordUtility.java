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
name|security
operator|.
name|user
package|;
end_package

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
name|NoSuchAlgorithmException
import|;
end_import

begin_comment
comment|/**  * PasswordUtility...  */
end_comment

begin_class
specifier|public
class|class
name|PasswordUtility
block|{
comment|/**      * logger instance      */
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
name|PasswordUtility
operator|.
name|class
argument_list|)
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
literal|10000
decl_stmt|;
comment|/**      * Avoid instantiation      */
specifier|private
name|PasswordUtility
parameter_list|()
block|{}
specifier|public
specifier|static
name|boolean
name|isSame
parameter_list|(
name|String
name|passwordHash
parameter_list|,
name|String
name|toTest
parameter_list|)
block|{
comment|// TODO
return|return
literal|false
return|;
block|}
specifier|public
specifier|static
name|String
name|buildPasswordHash
parameter_list|(
name|String
name|password
parameter_list|,
name|String
name|algorithm
parameter_list|,
name|int
name|defaultSaltSize
parameter_list|,
name|int
name|iterations
parameter_list|)
throws|throws
name|NoSuchAlgorithmException
throws|,
name|UnsupportedEncodingException
block|{
comment|// TODO
return|return
literal|null
return|;
block|}
specifier|public
specifier|static
name|boolean
name|isPlainTextPassword
parameter_list|(
name|String
name|password
parameter_list|)
block|{
comment|// TODO
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

