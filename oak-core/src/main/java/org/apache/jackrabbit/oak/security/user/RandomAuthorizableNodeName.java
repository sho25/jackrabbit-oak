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
name|security
operator|.
name|user
package|;
end_package

begin_import
import|import static
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
name|RegistrationConstants
operator|.
name|OAK_SECURITY_NAME
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
name|util
operator|.
name|Random
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
name|AuthorizableNodeName
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
name|osgi
operator|.
name|service
operator|.
name|component
operator|.
name|annotations
operator|.
name|Activate
import|;
end_import

begin_import
import|import
name|org
operator|.
name|osgi
operator|.
name|service
operator|.
name|component
operator|.
name|annotations
operator|.
name|Component
import|;
end_import

begin_import
import|import
name|org
operator|.
name|osgi
operator|.
name|service
operator|.
name|component
operator|.
name|annotations
operator|.
name|ConfigurationPolicy
import|;
end_import

begin_import
import|import
name|org
operator|.
name|osgi
operator|.
name|service
operator|.
name|metatype
operator|.
name|annotations
operator|.
name|AttributeDefinition
import|;
end_import

begin_import
import|import
name|org
operator|.
name|osgi
operator|.
name|service
operator|.
name|metatype
operator|.
name|annotations
operator|.
name|Designate
import|;
end_import

begin_import
import|import
name|org
operator|.
name|osgi
operator|.
name|service
operator|.
name|metatype
operator|.
name|annotations
operator|.
name|ObjectClassDefinition
import|;
end_import

begin_comment
comment|/**  * Implementation of the {@code AuthorizableNodeName} that generates a random  * node name that doesn't reveal the ID of the authorizable.  */
end_comment

begin_class
annotation|@
name|Component
argument_list|(
name|configurationPolicy
operator|=
name|ConfigurationPolicy
operator|.
name|REQUIRE
argument_list|,
name|service
operator|=
name|AuthorizableNodeName
operator|.
name|class
argument_list|,
name|property
operator|=
name|OAK_SECURITY_NAME
operator|+
literal|"=org.apache.jackrabbit.oak.security.user.RandomAuthorizableNodeName"
argument_list|)
annotation|@
name|Designate
argument_list|(
name|ocd
operator|=
name|RandomAuthorizableNodeName
operator|.
name|Configuration
operator|.
name|class
argument_list|)
specifier|public
class|class
name|RandomAuthorizableNodeName
implements|implements
name|AuthorizableNodeName
block|{
annotation|@
name|ObjectClassDefinition
argument_list|(
name|name
operator|=
literal|"Apache Jackrabbit Oak Random Authorizable Node Name"
argument_list|,
name|description
operator|=
literal|"Generates a random name for the authorizable node."
argument_list|)
annotation_defn|@interface
name|Configuration
block|{
annotation|@
name|AttributeDefinition
argument_list|(
name|name
operator|=
literal|"Name Length"
argument_list|,
name|description
operator|=
literal|"Length of the generated node name."
argument_list|)
name|int
name|length
parameter_list|()
default|default
name|DEFAULT_LENGTH
function_decl|;
block|}
comment|/**      * Characters used to encode the random data. This matches the Base64URL      * characters, which is both filename- and URL-safe.      */
specifier|private
specifier|static
specifier|final
name|char
index|[]
name|VALID_CHARS
decl_stmt|;
static|static
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|char
name|i
decl_stmt|;
for|for
control|(
name|i
operator|=
literal|'a'
init|;
name|i
operator|<=
literal|'z'
condition|;
name|i
operator|++
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|i
operator|=
literal|'A'
init|;
name|i
operator|<=
literal|'Z'
condition|;
name|i
operator|++
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|i
operator|=
literal|'0'
init|;
name|i
operator|<=
literal|'9'
condition|;
name|i
operator|++
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|"-_"
argument_list|)
expr_stmt|;
name|VALID_CHARS
operator|=
name|sb
operator|.
name|toString
argument_list|()
operator|.
name|toCharArray
argument_list|()
expr_stmt|;
block|}
comment|/**      * 21 characters, each character with 6 bit of entropy (64 possible      * characters), results in 126 bits of entropy. With regards to probability      * of duplicates, this is even better than standard UUIDs, which have 122      * bits of entropy and are 36 characters long.      */
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_LENGTH
init|=
literal|21
decl_stmt|;
specifier|private
name|int
name|length
init|=
name|DEFAULT_LENGTH
decl_stmt|;
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|String
name|generateNodeName
parameter_list|(
annotation|@
name|NotNull
name|String
name|authorizableId
parameter_list|)
block|{
name|Random
name|random
init|=
operator|new
name|SecureRandom
argument_list|()
decl_stmt|;
name|char
index|[]
name|chars
init|=
operator|new
name|char
index|[
name|length
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
name|length
condition|;
name|i
operator|++
control|)
block|{
name|chars
index|[
name|i
index|]
operator|=
name|VALID_CHARS
index|[
name|random
operator|.
name|nextInt
argument_list|(
name|VALID_CHARS
operator|.
name|length
argument_list|)
index|]
expr_stmt|;
block|}
return|return
operator|new
name|String
argument_list|(
name|chars
argument_list|)
return|;
block|}
annotation|@
name|Activate
specifier|private
name|void
name|activate
parameter_list|(
name|Configuration
name|config
parameter_list|)
block|{
name|length
operator|=
name|config
operator|.
name|length
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

