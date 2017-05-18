begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|run
operator|.
name|cli
package|;
end_package

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Field
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|annotation
operator|.
name|Nullable
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
name|Lists
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
name|Maps
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
name|primitives
operator|.
name|Ints
import|;
end_import

begin_import
import|import
name|joptsimple
operator|.
name|BuiltinHelpFormatter
import|;
end_import

begin_import
import|import
name|joptsimple
operator|.
name|HelpFormatter
import|;
end_import

begin_import
import|import
name|joptsimple
operator|.
name|OptionDescriptor
import|;
end_import

begin_import
import|import
name|joptsimple
operator|.
name|OptionSpec
import|;
end_import

begin_import
import|import
name|joptsimple
operator|.
name|internal
operator|.
name|Strings
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
name|base
operator|.
name|StandardSystemProperty
operator|.
name|LINE_SEPARATOR
import|;
end_import

begin_class
specifier|public
class|class
name|OakHelpFormatter
implements|implements
name|HelpFormatter
block|{
specifier|private
specifier|static
specifier|final
name|int
name|COL_WIDTH
init|=
literal|120
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|OptionsBean
argument_list|>
name|optionBeans
decl_stmt|;
specifier|private
specifier|final
name|String
name|commandName
decl_stmt|;
specifier|private
specifier|final
name|String
name|connectionString
decl_stmt|;
specifier|private
specifier|final
name|String
name|summary
decl_stmt|;
specifier|public
name|OakHelpFormatter
parameter_list|(
name|Iterable
argument_list|<
name|OptionsBean
argument_list|>
name|optionBeans
parameter_list|,
annotation|@
name|Nullable
name|String
name|commandName
parameter_list|,
annotation|@
name|Nullable
name|String
name|summary
parameter_list|,
annotation|@
name|Nullable
name|String
name|connectionString
parameter_list|)
block|{
name|this
operator|.
name|optionBeans
operator|=
name|Lists
operator|.
name|newArrayList
argument_list|(
name|optionBeans
argument_list|)
expr_stmt|;
name|this
operator|.
name|commandName
operator|=
name|commandName
expr_stmt|;
name|this
operator|.
name|summary
operator|=
name|summary
expr_stmt|;
name|this
operator|.
name|connectionString
operator|=
name|connectionString
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|format
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|?
extends|extends
name|OptionDescriptor
argument_list|>
name|options
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|?
extends|extends
name|OptionDescriptor
argument_list|>
name|clonedOptions
init|=
name|Maps
operator|.
name|newHashMap
argument_list|(
name|options
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|OptionCategory
argument_list|>
name|optionCategories
init|=
name|categorise
argument_list|(
name|clonedOptions
argument_list|)
decl_stmt|;
comment|//TODO Take care of left over options
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|append
argument_list|(
operator|new
name|MainSectionFormatter
argument_list|()
operator|.
name|format
argument_list|(
name|options
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
name|LINE_SEPARATOR
operator|.
name|value
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|OptionCategory
name|c
range|:
name|optionCategories
control|)
block|{
name|builder
operator|.
name|append
argument_list|(
name|c
operator|.
name|format
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
name|LINE_SEPARATOR
operator|.
name|value
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|private
name|List
argument_list|<
name|OptionCategory
argument_list|>
name|categorise
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|?
extends|extends
name|OptionDescriptor
argument_list|>
name|options
parameter_list|)
block|{
name|List
argument_list|<
name|OptionCategory
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|OptionsBean
name|bean
range|:
name|optionBeans
control|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|OptionDescriptor
argument_list|>
name|optsForThisBean
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|OptionDescriptor
argument_list|>
name|operationsForThisBean
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|getOptionNames
argument_list|(
name|bean
argument_list|)
control|)
block|{
name|OptionDescriptor
name|desc
init|=
name|options
operator|.
name|remove
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|desc
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|bean
operator|.
name|operationNames
argument_list|()
operator|.
name|contains
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|operationsForThisBean
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|desc
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|optsForThisBean
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|desc
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|result
operator|.
name|add
argument_list|(
operator|new
name|OptionCategory
argument_list|(
name|bean
argument_list|,
name|optsForThisBean
argument_list|,
name|operationsForThisBean
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Collections
operator|.
name|sort
argument_list|(
name|result
argument_list|,
name|Collections
operator|.
name|reverseOrder
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
specifier|private
specifier|static
name|int
name|getColWidth
parameter_list|()
block|{
return|return
name|COL_WIDTH
return|;
block|}
specifier|private
specifier|static
name|Set
argument_list|<
name|String
argument_list|>
name|getOptionNames
parameter_list|(
name|OptionsBean
name|bean
parameter_list|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|names
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
try|try
block|{
for|for
control|(
name|Field
name|field
range|:
name|bean
operator|.
name|getClass
argument_list|()
operator|.
name|getDeclaredFields
argument_list|()
control|)
block|{
if|if
condition|(
name|OptionSpec
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|field
operator|.
name|getType
argument_list|()
argument_list|)
condition|)
block|{
name|field
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|OptionSpec
name|spec
init|=
operator|(
name|OptionSpec
operator|)
name|field
operator|.
name|get
argument_list|(
name|bean
argument_list|)
decl_stmt|;
name|names
operator|.
name|addAll
argument_list|(
name|spec
operator|.
name|options
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
return|return
name|names
return|;
block|}
specifier|private
specifier|static
class|class
name|OptionCategory
implements|implements
name|Comparable
argument_list|<
name|OptionCategory
argument_list|>
block|{
specifier|final
name|OptionsBean
name|bean
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|?
extends|extends
name|OptionDescriptor
argument_list|>
name|options
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|?
extends|extends
name|OptionDescriptor
argument_list|>
name|operations
decl_stmt|;
specifier|public
name|OptionCategory
parameter_list|(
name|OptionsBean
name|bean
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|?
extends|extends
name|OptionDescriptor
argument_list|>
name|options
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|OptionDescriptor
argument_list|>
name|operations
parameter_list|)
block|{
name|this
operator|.
name|bean
operator|=
name|bean
expr_stmt|;
name|this
operator|.
name|options
operator|=
name|options
expr_stmt|;
name|this
operator|.
name|operations
operator|=
name|operations
expr_stmt|;
block|}
specifier|public
name|String
name|format
parameter_list|()
block|{
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|append
argument_list|(
operator|new
name|CategoryFormatter
argument_list|(
name|bean
argument_list|)
operator|.
name|format
argument_list|(
name|options
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|operations
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|builder
operator|.
name|append
argument_list|(
name|LINE_SEPARATOR
operator|.
name|value
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
operator|new
name|OperationsFormatter
argument_list|()
operator|.
name|format
argument_list|(
name|operations
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|compareTo
parameter_list|(
name|OptionCategory
name|that
parameter_list|)
block|{
return|return
name|Ints
operator|.
name|compare
argument_list|(
name|this
operator|.
name|bean
operator|.
name|order
argument_list|()
argument_list|,
name|that
operator|.
name|bean
operator|.
name|order
argument_list|()
argument_list|)
return|;
block|}
block|}
specifier|private
specifier|static
class|class
name|CategoryFormatter
extends|extends
name|BuiltinHelpFormatter
block|{
specifier|final
name|OptionsBean
name|bean
decl_stmt|;
specifier|public
name|CategoryFormatter
parameter_list|(
name|OptionsBean
name|bean
parameter_list|)
block|{
name|super
argument_list|(
name|getColWidth
argument_list|()
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|this
operator|.
name|bean
operator|=
name|bean
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|addRows
parameter_list|(
name|Collection
argument_list|<
name|?
extends|extends
name|OptionDescriptor
argument_list|>
name|options
parameter_list|)
block|{
name|addHeader
argument_list|()
expr_stmt|;
name|super
operator|.
name|addRows
argument_list|(
name|options
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|addNonOptionsDescription
parameter_list|(
name|Collection
argument_list|<
name|?
extends|extends
name|OptionDescriptor
argument_list|>
name|options
parameter_list|)
block|{
comment|//Noop call as category options do not specify non options
block|}
specifier|private
name|void
name|addHeader
parameter_list|()
block|{
name|String
name|title
init|=
name|bean
operator|.
name|title
argument_list|()
decl_stmt|;
if|if
condition|(
name|title
operator|!=
literal|null
condition|)
block|{
name|addNonOptionRow
argument_list|(
name|title
argument_list|)
expr_stmt|;
name|addNonOptionRow
argument_list|(
name|Strings
operator|.
name|repeat
argument_list|(
literal|'='
argument_list|,
name|title
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|bean
operator|.
name|description
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|addNonOptionRow
argument_list|(
name|bean
operator|.
name|description
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**      * Used for rending options which are "operations"      */
specifier|private
specifier|static
class|class
name|OperationsFormatter
extends|extends
name|BuiltinHelpFormatter
block|{
specifier|public
specifier|static
specifier|final
name|String
name|OPERATIONS
init|=
literal|"Operations"
decl_stmt|;
specifier|public
name|OperationsFormatter
parameter_list|()
block|{
name|super
argument_list|(
name|getColWidth
argument_list|()
argument_list|,
literal|2
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|addHeaders
parameter_list|(
name|Collection
argument_list|<
name|?
extends|extends
name|OptionDescriptor
argument_list|>
name|options
parameter_list|)
block|{
name|addOptionRow
argument_list|(
name|OPERATIONS
argument_list|,
name|message
argument_list|(
literal|"description.header"
argument_list|)
argument_list|)
expr_stmt|;
name|addOptionRow
argument_list|(
name|Strings
operator|.
name|repeat
argument_list|(
literal|'-'
argument_list|,
name|OPERATIONS
operator|.
name|length
argument_list|()
argument_list|)
argument_list|,
name|message
argument_list|(
literal|"description.divider"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|addNonOptionsDescription
parameter_list|(
name|Collection
argument_list|<
name|?
extends|extends
name|OptionDescriptor
argument_list|>
name|options
parameter_list|)
block|{
comment|//Noop call as category options do not specify non options
block|}
block|}
comment|/**      * Formatter for the first section of the help. It dumps the connection string, command      * and summary only. No options are handled by this formatter      */
specifier|private
class|class
name|MainSectionFormatter
extends|extends
name|BuiltinHelpFormatter
block|{
specifier|public
name|MainSectionFormatter
parameter_list|()
block|{
name|super
argument_list|(
name|getColWidth
argument_list|()
argument_list|,
literal|2
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|addRows
parameter_list|(
name|Collection
argument_list|<
name|?
extends|extends
name|OptionDescriptor
argument_list|>
name|options
parameter_list|)
block|{
name|String
name|firstLine
init|=
name|commandName
operator|!=
literal|null
condition|?
name|commandName
operator|+
literal|" "
else|:
literal|""
decl_stmt|;
if|if
condition|(
name|connectionString
operator|!=
literal|null
condition|)
block|{
name|firstLine
operator|+=
name|connectionString
expr_stmt|;
block|}
name|addNonOptionRow
argument_list|(
name|firstLine
argument_list|)
expr_stmt|;
if|if
condition|(
name|summary
operator|!=
literal|null
condition|)
block|{
name|addNonOptionRow
argument_list|(
name|summary
argument_list|)
expr_stmt|;
block|}
name|fitRowsToWidth
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

