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
name|authentication
operator|.
name|credentials
package|;
end_package

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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Credentials
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|SimpleCredentials
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
name|ImmutableMap
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
name|ImmutableSet
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
name|assertNotNull
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
name|assertNull
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Sets
operator|.
name|newHashSet
import|;
end_import

begin_class
specifier|public
class|class
name|CompositeCredentialsSupportTest
block|{
specifier|private
specifier|final
name|TestCredentialsSupport
name|tcs
init|=
operator|new
name|TestCredentialsSupport
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|CredentialsSupport
name|credentialsSupport
init|=
name|CompositeCredentialsSupport
operator|.
name|newInstance
argument_list|(
parameter_list|()
lambda|->
name|newHashSet
argument_list|(
name|SimpleCredentialsSupport
operator|.
name|getInstance
argument_list|()
argument_list|,
name|tcs
argument_list|)
argument_list|)
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|testGetCredentialClasses
parameter_list|()
block|{
name|Set
argument_list|<
name|Class
argument_list|>
name|supported
init|=
name|credentialsSupport
operator|.
name|getCredentialClasses
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|supported
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|supported
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|supported
operator|.
name|contains
argument_list|(
name|TestCredentials
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|supported
operator|.
name|contains
argument_list|(
name|SimpleCredentials
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetUserId
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|"Test1CredentialsSupport"
argument_list|,
name|credentialsSupport
operator|.
name|getUserId
argument_list|(
operator|new
name|TestCredentials
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|credentialsSupport
operator|.
name|getUserId
argument_list|(
operator|new
name|SimpleCredentials
argument_list|(
literal|null
argument_list|,
operator|new
name|char
index|[
literal|0
index|]
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"uid"
argument_list|,
name|credentialsSupport
operator|.
name|getUserId
argument_list|(
operator|new
name|SimpleCredentials
argument_list|(
literal|"uid"
argument_list|,
operator|new
name|char
index|[
literal|0
index|]
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|credentialsSupport
operator|.
name|getUserId
argument_list|(
operator|new
name|Credentials
argument_list|()
block|{         }
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetAttributes
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|?
argument_list|>
name|attributes
init|=
name|credentialsSupport
operator|.
name|getAttributes
argument_list|(
operator|new
name|TestCredentials
argument_list|()
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|attributes
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|attributes
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|SimpleCredentials
name|sc
init|=
operator|new
name|SimpleCredentials
argument_list|(
literal|"uid"
argument_list|,
operator|new
name|char
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|attributes
operator|=
name|credentialsSupport
operator|.
name|getAttributes
argument_list|(
name|sc
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|attributes
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|attributes
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|?
argument_list|>
name|expected
init|=
name|ImmutableMap
operator|.
name|of
argument_list|(
literal|"a"
argument_list|,
literal|"a"
argument_list|,
literal|"b"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|,
literal|"c"
argument_list|,
operator|new
name|TestCredentials
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|?
argument_list|>
name|entry
range|:
name|expected
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|sc
operator|.
name|setAttribute
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|attributes
operator|=
name|credentialsSupport
operator|.
name|getAttributes
argument_list|(
name|sc
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|attributes
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|attributes
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|attributes
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSetAttributes
parameter_list|()
block|{
name|SimpleCredentials
name|sc
init|=
operator|new
name|SimpleCredentials
argument_list|(
literal|"uid"
argument_list|,
operator|new
name|char
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|TestCredentials
name|tc
init|=
operator|new
name|TestCredentials
argument_list|()
decl_stmt|;
name|Credentials
name|dummy
init|=
operator|new
name|Credentials
argument_list|()
block|{         }
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|?
argument_list|>
name|attributesS
init|=
name|credentialsSupport
operator|.
name|getAttributes
argument_list|(
name|sc
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|attributesS
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|attributesS
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|?
argument_list|>
name|attributesT
init|=
name|credentialsSupport
operator|.
name|getAttributes
argument_list|(
name|tc
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|attributesT
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|attributesT
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|?
argument_list|>
name|attributesD
init|=
name|credentialsSupport
operator|.
name|getAttributes
argument_list|(
name|dummy
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|attributesD
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|attributesD
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|?
argument_list|>
name|expectedS
init|=
name|ImmutableMap
operator|.
name|of
argument_list|(
literal|"a"
argument_list|,
literal|"a"
argument_list|,
literal|"b"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|,
literal|"c"
argument_list|,
operator|new
name|TestCredentials
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|credentialsSupport
operator|.
name|setAttributes
argument_list|(
name|sc
argument_list|,
name|expectedS
argument_list|)
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|?
argument_list|>
name|expectedT
init|=
name|ImmutableMap
operator|.
name|of
argument_list|(
literal|"test"
argument_list|,
literal|"Test1CredentialsSupport"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|credentialsSupport
operator|.
name|setAttributes
argument_list|(
name|tc
argument_list|,
name|expectedT
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|credentialsSupport
operator|.
name|setAttributes
argument_list|(
name|dummy
argument_list|,
name|ImmutableMap
operator|.
name|of
argument_list|(
literal|"none"
argument_list|,
literal|"none"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|attributesS
operator|=
name|credentialsSupport
operator|.
name|getAttributes
argument_list|(
name|sc
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|?
argument_list|>
name|entry
range|:
name|expectedS
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|assertEquals
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|,
name|attributesS
operator|.
name|get
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|attributesT
operator|=
name|credentialsSupport
operator|.
name|getAttributes
argument_list|(
name|tc
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|?
argument_list|>
name|entry
range|:
name|expectedT
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|assertEquals
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|,
name|attributesT
operator|.
name|get
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|attributesD
operator|=
name|credentialsSupport
operator|.
name|getAttributes
argument_list|(
name|dummy
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|attributesD
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|attributesD
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
specifier|final
class|class
name|TestCredentials
implements|implements
name|Credentials
block|{     }
specifier|private
specifier|static
specifier|final
class|class
name|TestCredentialsSupport
implements|implements
name|CredentialsSupport
block|{
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|attributes
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|Set
argument_list|<
name|Class
argument_list|>
name|getCredentialClasses
parameter_list|()
block|{
return|return
name|ImmutableSet
operator|.
name|of
argument_list|(
name|TestCredentials
operator|.
name|class
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getUserId
parameter_list|(
name|Credentials
name|credentials
parameter_list|)
block|{
if|if
condition|(
name|credentials
operator|instanceof
name|TestCredentials
condition|)
block|{
return|return
literal|"Test1CredentialsSupport"
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|?
argument_list|>
name|getAttributes
parameter_list|(
name|Credentials
name|credentials
parameter_list|)
block|{
if|if
condition|(
name|credentials
operator|instanceof
name|TestCredentials
condition|)
block|{
return|return
name|attributes
return|;
block|}
else|else
block|{
return|return
name|ImmutableMap
operator|.
name|of
argument_list|()
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|setAttributes
parameter_list|(
name|Credentials
name|credentials
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|?
argument_list|>
name|attributes
parameter_list|)
block|{
if|if
condition|(
name|credentials
operator|instanceof
name|TestCredentials
condition|)
block|{
name|this
operator|.
name|attributes
operator|.
name|putAll
argument_list|(
name|attributes
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
block|}
end_class

end_unit
