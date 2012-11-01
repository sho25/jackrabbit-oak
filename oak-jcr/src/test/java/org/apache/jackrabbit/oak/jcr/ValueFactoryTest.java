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
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|PropertyType
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|RepositoryException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|ValueFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|ValueFormatException
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
name|fail
import|;
end_import

begin_comment
comment|/**  * ValueFactoryTest...  */
end_comment

begin_class
specifier|public
class|class
name|ValueFactoryTest
extends|extends
name|AbstractRepositoryTest
block|{
specifier|private
name|ValueFactory
name|valueFactory
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|valueFactory
operator|=
name|getAdminSession
argument_list|()
operator|.
name|getValueFactory
argument_list|()
expr_stmt|;
block|}
comment|// TODO: this tests should be moved to the TCK. retrieving "invalidIdentifier" from the config.
annotation|@
name|Test
specifier|public
name|void
name|testReferenceValue
parameter_list|()
block|{
try|try
block|{
name|valueFactory
operator|.
name|createValue
argument_list|(
literal|"invalidIdentifier"
argument_list|,
name|PropertyType
operator|.
name|REFERENCE
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Conversion to REFERENCE value must validate identifier string "
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ValueFormatException
name|e
parameter_list|)
block|{
comment|// success
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testWeakReferenceValue
parameter_list|()
block|{
try|try
block|{
name|valueFactory
operator|.
name|createValue
argument_list|(
literal|"invalidIdentifier"
argument_list|,
name|PropertyType
operator|.
name|WEAKREFERENCE
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Conversion to WEAK_REFERENCE value must validate identifier string "
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ValueFormatException
name|e
parameter_list|)
block|{
comment|// success
block|}
block|}
block|}
end_class

end_unit

