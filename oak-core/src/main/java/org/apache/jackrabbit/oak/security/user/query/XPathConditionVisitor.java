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
operator|.
name|query
package|;
end_package

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
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|namepath
operator|.
name|NamePathMapper
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

begin_comment
comment|/**  * XPATH based condition visitor.  */
end_comment

begin_class
class|class
name|XPathConditionVisitor
implements|implements
name|ConditionVisitor
block|{
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|XPathConditionVisitor
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|StringBuilder
name|statement
decl_stmt|;
specifier|private
specifier|final
name|NamePathMapper
name|namePathMapper
decl_stmt|;
name|XPathConditionVisitor
parameter_list|(
name|StringBuilder
name|statement
parameter_list|,
name|NamePathMapper
name|namePathMapper
parameter_list|)
block|{
name|this
operator|.
name|statement
operator|=
name|statement
expr_stmt|;
name|this
operator|.
name|namePathMapper
operator|=
name|namePathMapper
expr_stmt|;
block|}
comment|//---------------------------------------------------< ConditionVisitor>---
annotation|@
name|Override
specifier|public
name|void
name|visit
parameter_list|(
name|Condition
operator|.
name|Node
name|condition
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|statement
operator|.
name|append
argument_list|(
literal|'('
argument_list|)
operator|.
name|append
argument_list|(
literal|"jcr:like(@"
argument_list|)
operator|.
name|append
argument_list|(
name|namePathMapper
operator|.
name|getJcrName
argument_list|(
name|UserConstants
operator|.
name|REP_PRINCIPAL_NAME
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|",'"
argument_list|)
operator|.
name|append
argument_list|(
name|condition
operator|.
name|getPattern
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"')"
argument_list|)
operator|.
name|append
argument_list|(
literal|" or "
argument_list|)
operator|.
name|append
argument_list|(
literal|"jcr:like(fn:name(),'"
argument_list|)
operator|.
name|append
argument_list|(
name|QueryUtil
operator|.
name|escapeNodeName
argument_list|(
name|condition
operator|.
name|getPattern
argument_list|()
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|"')"
argument_list|)
operator|.
name|append
argument_list|(
literal|')'
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|visit
parameter_list|(
name|Condition
operator|.
name|Property
name|condition
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|RelationOp
name|relOp
init|=
name|condition
operator|.
name|getOp
argument_list|()
decl_stmt|;
if|if
condition|(
name|relOp
operator|==
name|RelationOp
operator|.
name|EX
condition|)
block|{
name|statement
operator|.
name|append
argument_list|(
name|condition
operator|.
name|getRelPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|relOp
operator|==
name|RelationOp
operator|.
name|LIKE
condition|)
block|{
name|statement
operator|.
name|append
argument_list|(
literal|"jcr:like("
argument_list|)
operator|.
name|append
argument_list|(
name|condition
operator|.
name|getRelPath
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|",'"
argument_list|)
operator|.
name|append
argument_list|(
name|condition
operator|.
name|getPattern
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"')"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|statement
operator|.
name|append
argument_list|(
name|condition
operator|.
name|getRelPath
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
name|condition
operator|.
name|getOp
argument_list|()
operator|.
name|getOp
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
name|QueryUtil
operator|.
name|format
argument_list|(
name|condition
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|visit
parameter_list|(
name|Condition
operator|.
name|Contains
name|condition
parameter_list|)
block|{
name|statement
operator|.
name|append
argument_list|(
literal|"jcr:contains("
argument_list|)
operator|.
name|append
argument_list|(
name|condition
operator|.
name|getRelPath
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|",'"
argument_list|)
operator|.
name|append
argument_list|(
name|condition
operator|.
name|getSearchExpr
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"')"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|visit
parameter_list|(
name|Condition
operator|.
name|Impersonation
name|condition
parameter_list|)
block|{
name|statement
operator|.
name|append
argument_list|(
literal|"@rep:impersonators='"
argument_list|)
operator|.
name|append
argument_list|(
name|condition
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|'\''
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|visit
parameter_list|(
name|Condition
operator|.
name|Not
name|condition
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|statement
operator|.
name|append
argument_list|(
literal|"not("
argument_list|)
expr_stmt|;
name|condition
operator|.
name|getCondition
argument_list|()
operator|.
name|accept
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|statement
operator|.
name|append
argument_list|(
literal|')'
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|visit
parameter_list|(
name|Condition
operator|.
name|And
name|condition
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|int
name|count
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Condition
name|c
range|:
name|condition
control|)
block|{
name|statement
operator|.
name|append
argument_list|(
name|count
operator|++
operator|>
literal|0
condition|?
literal|" and "
else|:
literal|""
argument_list|)
expr_stmt|;
name|c
operator|.
name|accept
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|visit
parameter_list|(
name|Condition
operator|.
name|Or
name|condition
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|int
name|pos
init|=
name|statement
operator|.
name|length
argument_list|()
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Condition
name|c
range|:
name|condition
control|)
block|{
name|statement
operator|.
name|append
argument_list|(
name|count
operator|++
operator|>
literal|0
condition|?
literal|" or "
else|:
literal|""
argument_list|)
expr_stmt|;
name|c
operator|.
name|accept
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
comment|// Surround or clause with parentheses if it contains more than one term
if|if
condition|(
name|count
operator|>
literal|1
condition|)
block|{
name|statement
operator|.
name|insert
argument_list|(
name|pos
argument_list|,
literal|'('
argument_list|)
expr_stmt|;
name|statement
operator|.
name|append
argument_list|(
literal|')'
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

