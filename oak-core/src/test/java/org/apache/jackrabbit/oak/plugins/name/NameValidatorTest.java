begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|plugins
operator|.
name|name
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
name|plugins
operator|.
name|memory
operator|.
name|EmptyNodeState
operator|.
name|EMPTY_NODE
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
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|api
operator|.
name|CommitFailedException
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
name|commit
operator|.
name|Validator
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

begin_class
specifier|public
class|class
name|NameValidatorTest
block|{
specifier|private
specifier|final
name|Validator
name|validator
init|=
operator|new
name|NameValidator
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
literal|"valid"
argument_list|)
argument_list|)
decl_stmt|;
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|CommitFailedException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testCurrentPath
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|validator
operator|.
name|childNodeAdded
argument_list|(
literal|"."
argument_list|,
name|EMPTY_NODE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|CommitFailedException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testParentPath
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|validator
operator|.
name|childNodeAdded
argument_list|(
literal|".."
argument_list|,
name|EMPTY_NODE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
comment|// valid as of OAK-182
specifier|public
name|void
name|testEmptyPrefix
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|validator
operator|.
name|childNodeAdded
argument_list|(
literal|":name"
argument_list|,
name|EMPTY_NODE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|CommitFailedException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testInvalidPrefix
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|validator
operator|.
name|childNodeAdded
argument_list|(
literal|"invalid:name"
argument_list|,
name|EMPTY_NODE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|CommitFailedException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testTrailingWhitespace
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|validator
operator|.
name|childNodeAdded
argument_list|(
literal|"name "
argument_list|,
name|EMPTY_NODE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|CommitFailedException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testLeadingWhitespace
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|validator
operator|.
name|childNodeAdded
argument_list|(
literal|" name"
argument_list|,
name|EMPTY_NODE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|CommitFailedException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testOnlyWhitespace
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|validator
operator|.
name|childNodeAdded
argument_list|(
literal|" "
argument_list|,
name|EMPTY_NODE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testValidPrefix
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|validator
operator|.
name|childNodeAdded
argument_list|(
literal|"valid:name"
argument_list|,
name|EMPTY_NODE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|CommitFailedException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testSlashName
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|validator
operator|.
name|childNodeAdded
argument_list|(
literal|"invalid/name"
argument_list|,
name|EMPTY_NODE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testValidIndexInName
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|validator
operator|.
name|childNodeAdded
argument_list|(
literal|"name[1]"
argument_list|,
name|EMPTY_NODE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|CommitFailedException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testInvalidIndexInName
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|validator
operator|.
name|childNodeAdded
argument_list|(
literal|"name[x]"
argument_list|,
name|EMPTY_NODE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testValidName
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|validator
operator|.
name|childNodeAdded
argument_list|(
literal|"name"
argument_list|,
name|EMPTY_NODE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|CommitFailedException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testNameWithLineBreaks
parameter_list|()
throws|throws
name|Exception
block|{
name|validator
operator|.
name|childNodeAdded
argument_list|(
literal|"name\tx"
argument_list|,
name|EMPTY_NODE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDeleted
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|validator
operator|.
name|childNodeDeleted
argument_list|(
literal|"."
argument_list|,
name|EMPTY_NODE
argument_list|)
expr_stmt|;
name|validator
operator|.
name|childNodeDeleted
argument_list|(
literal|".."
argument_list|,
name|EMPTY_NODE
argument_list|)
expr_stmt|;
name|validator
operator|.
name|childNodeDeleted
argument_list|(
literal|"valid:name"
argument_list|,
name|EMPTY_NODE
argument_list|)
expr_stmt|;
name|validator
operator|.
name|childNodeDeleted
argument_list|(
literal|"invalid:name"
argument_list|,
name|EMPTY_NODE
argument_list|)
expr_stmt|;
name|validator
operator|.
name|childNodeDeleted
argument_list|(
literal|"invalid/name"
argument_list|,
name|EMPTY_NODE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testEscaping
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|"abc"
argument_list|,
name|NameValidator
operator|.
name|getPrintableName
argument_list|(
literal|"abc"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"\\t\\r\\n\\b\\f"
argument_list|,
name|NameValidator
operator|.
name|getPrintableName
argument_list|(
literal|"\t\r\n\b\f"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"\\u00e0"
argument_list|,
name|NameValidator
operator|.
name|getPrintableName
argument_list|(
literal|"\u00e0"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

