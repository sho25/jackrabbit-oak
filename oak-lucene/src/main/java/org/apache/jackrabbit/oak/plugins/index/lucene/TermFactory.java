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
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|Term
import|;
end_import

begin_comment
comment|/**  * {@code TermFactory} is a factory for<code>Term</code> instances with  * frequently used field names.  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|TermFactory
block|{
comment|/**      * Private constructor.      */
specifier|private
name|TermFactory
parameter_list|()
block|{     }
comment|/**      * Creates a Term with the given {@code path} value and with a field      * name {@link FieldNames#PATH}.      *      * @param path      *            the path.      * @return the path term.      */
specifier|public
specifier|static
name|Term
name|newPathTerm
parameter_list|(
name|String
name|path
parameter_list|)
block|{
return|return
operator|new
name|Term
argument_list|(
name|FieldNames
operator|.
name|PATH
argument_list|,
name|preparePath
argument_list|(
name|path
argument_list|)
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|Term
name|newFulltextTerm
parameter_list|(
name|String
name|ft
parameter_list|,
name|String
name|field
parameter_list|)
block|{
if|if
condition|(
name|field
operator|==
literal|null
operator|||
literal|"*"
operator|.
name|equals
argument_list|(
name|field
argument_list|)
condition|)
block|{
return|return
name|newFulltextTerm
argument_list|(
name|ft
argument_list|)
return|;
block|}
return|return
operator|new
name|Term
argument_list|(
name|field
argument_list|,
name|ft
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|Term
name|newAncestorTerm
parameter_list|(
name|String
name|path
parameter_list|)
block|{
return|return
operator|new
name|Term
argument_list|(
name|FieldNames
operator|.
name|ANCESTORS
argument_list|,
name|preparePath
argument_list|(
name|path
argument_list|)
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|Term
name|newFulltextTerm
parameter_list|(
name|String
name|ft
parameter_list|)
block|{
return|return
operator|new
name|Term
argument_list|(
name|FieldNames
operator|.
name|FULLTEXT
argument_list|,
name|ft
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|String
name|preparePath
parameter_list|(
name|String
name|path
parameter_list|)
block|{
if|if
condition|(
operator|!
literal|"/"
operator|.
name|equals
argument_list|(
name|path
argument_list|)
operator|&&
operator|!
name|path
operator|.
name|startsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
name|path
operator|=
literal|"/"
operator|+
name|path
expr_stmt|;
block|}
return|return
name|path
return|;
block|}
block|}
end_class

end_unit

