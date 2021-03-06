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
name|solr
operator|.
name|util
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

begin_comment
comment|/**  * Solr utility class  */
end_comment

begin_class
specifier|public
class|class
name|SolrUtils
block|{
comment|/**      * Escape a char sequence in order to make it usable within a Solr query      *      * @param s the String to escape      * @return an escaped String      */
specifier|public
specifier|static
name|CharSequence
name|partialEscape
parameter_list|(
name|CharSequence
name|s
parameter_list|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
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
name|s
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|char
name|c
init|=
name|s
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|==
literal|'\\'
operator|||
name|c
operator|==
literal|'!'
operator|||
name|c
operator|==
literal|'('
operator|||
name|c
operator|==
literal|')'
operator|||
name|c
operator|==
literal|':'
operator|||
name|c
operator|==
literal|'^'
operator|||
name|c
operator|==
literal|'['
operator|||
name|c
operator|==
literal|']'
operator|||
name|c
operator|==
literal|'/'
operator|||
name|c
operator|==
literal|'{'
operator|||
name|c
operator|==
literal|'}'
operator|||
name|c
operator|==
literal|'~'
operator|||
name|c
operator|==
literal|'*'
operator|||
name|c
operator|==
literal|'?'
operator|||
name|c
operator|==
literal|'-'
operator|||
name|c
operator|==
literal|' '
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|'\\'
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
return|;
block|}
comment|/**      * Get the name of the field to be used for sorting a certain property      *      * @param tag          the {@code Type#tag} of the given property      * @param propertyName the name of the given property      * @return the name of the Solr field to be used for sorting on the given property      */
specifier|public
specifier|static
name|String
name|getSortingField
parameter_list|(
name|int
name|tag
parameter_list|,
name|String
name|propertyName
parameter_list|)
block|{
switch|switch
condition|(
name|tag
condition|)
block|{
case|case
name|PropertyType
operator|.
name|BINARY
case|:
return|return
name|propertyName
operator|+
literal|"_binary_sort"
return|;
case|case
name|PropertyType
operator|.
name|DOUBLE
case|:
return|return
name|propertyName
operator|+
literal|"_double_sort"
return|;
case|case
name|PropertyType
operator|.
name|DECIMAL
case|:
return|return
name|propertyName
operator|+
literal|"_double_sort"
return|;
default|default:
return|return
name|propertyName
operator|+
literal|"_string_sort"
return|;
block|}
block|}
block|}
end_class

end_unit

