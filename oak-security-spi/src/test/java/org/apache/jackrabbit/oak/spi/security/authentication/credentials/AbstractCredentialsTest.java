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
name|Date
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
name|ImmutableMap
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
name|assertNotSame
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

begin_class
specifier|public
class|class
name|AbstractCredentialsTest
block|{
specifier|private
specifier|static
specifier|final
name|String
name|USER_ID
init|=
literal|"userId"
decl_stmt|;
specifier|private
name|AbstractCredentials
name|credentials
init|=
operator|new
name|AbstractCredentials
argument_list|(
name|USER_ID
argument_list|)
block|{     }
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|testGetUserId
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|USER_ID
argument_list|,
name|credentials
operator|.
name|getUserId
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAttributesAreEmpty
parameter_list|()
block|{
name|assertTrue
argument_list|(
name|credentials
operator|.
name|getAttributes
argument_list|()
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|UnsupportedOperationException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testAttributesAreImmutable
parameter_list|()
block|{
name|credentials
operator|.
name|getAttributes
argument_list|()
operator|.
name|put
argument_list|(
literal|"attr"
argument_list|,
literal|"value"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSetAttribute
parameter_list|()
block|{
name|Object
name|value
init|=
operator|new
name|Date
argument_list|()
decl_stmt|;
name|credentials
operator|.
name|setAttribute
argument_list|(
literal|"attr"
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|value
argument_list|,
name|credentials
operator|.
name|getAttribute
argument_list|(
literal|"attr"
argument_list|)
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|attributes
init|=
name|credentials
operator|.
name|getAttributes
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|attributes
operator|.
name|containsKey
argument_list|(
literal|"attr"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|attributes
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|value
argument_list|,
name|attributes
operator|.
name|get
argument_list|(
literal|"attr"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSetNullAttributeValue
parameter_list|()
block|{
name|credentials
operator|.
name|setAttribute
argument_list|(
literal|"attr"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|credentials
operator|.
name|getAttributes
argument_list|()
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|credentials
operator|.
name|setAttribute
argument_list|(
literal|"attr"
argument_list|,
literal|25
argument_list|)
expr_stmt|;
name|credentials
operator|.
name|setAttribute
argument_list|(
literal|"attr"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|credentials
operator|.
name|getAttribute
argument_list|(
literal|"attr"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|credentials
operator|.
name|getAttributes
argument_list|()
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IllegalArgumentException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testSetNullAttributeName
parameter_list|()
block|{
name|credentials
operator|.
name|setAttribute
argument_list|(
literal|null
argument_list|,
literal|"value"
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
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|attributes
init|=
name|ImmutableMap
operator|.
name|of
argument_list|(
literal|"attr"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|credentials
operator|.
name|setAttributes
argument_list|(
name|attributes
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|attr
init|=
name|credentials
operator|.
name|getAttributes
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
name|attr
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|attributes
argument_list|,
name|attr
argument_list|)
expr_stmt|;
name|assertNotSame
argument_list|(
name|attributes
argument_list|,
name|attr
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

