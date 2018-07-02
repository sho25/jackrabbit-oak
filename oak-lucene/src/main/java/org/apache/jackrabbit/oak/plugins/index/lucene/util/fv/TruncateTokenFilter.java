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
name|index
operator|.
name|lucene
operator|.
name|util
operator|.
name|fv
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|TokenFilter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|TokenStream
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|tokenattributes
operator|.
name|CharTermAttribute
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|tokenattributes
operator|.
name|KeywordAttribute
import|;
end_import

begin_comment
comment|/**  * {@link TokenFilter} which truncates a token bigger than {#length}.  */
end_comment

begin_class
class|class
name|TruncateTokenFilter
extends|extends
name|TokenFilter
block|{
specifier|private
specifier|final
name|CharTermAttribute
name|termAttribute
init|=
name|addAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|KeywordAttribute
name|keywordAttr
init|=
name|addAttribute
argument_list|(
name|KeywordAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|int
name|length
decl_stmt|;
name|TruncateTokenFilter
parameter_list|(
name|TokenStream
name|input
parameter_list|,
name|int
name|length
parameter_list|)
block|{
name|super
argument_list|(
name|input
argument_list|)
expr_stmt|;
if|if
condition|(
name|length
operator|<
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"length parameter must be a positive number: "
operator|+
name|length
argument_list|)
throw|;
block|}
name|this
operator|.
name|length
operator|=
name|length
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
specifier|final
name|boolean
name|incrementToken
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|input
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
if|if
condition|(
operator|!
name|keywordAttr
operator|.
name|isKeyword
argument_list|()
operator|&&
name|termAttribute
operator|.
name|length
argument_list|()
operator|>
name|length
condition|)
block|{
name|termAttribute
operator|.
name|setLength
argument_list|(
name|length
argument_list|)
expr_stmt|;
block|}
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
end_class

end_unit
