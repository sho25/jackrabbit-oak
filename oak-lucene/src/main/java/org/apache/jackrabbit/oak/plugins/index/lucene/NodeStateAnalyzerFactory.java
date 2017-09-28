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
name|plugins
operator|.
name|index
operator|.
name|lucene
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
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStreamReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Constructor
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|InvocationTargetException
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicBoolean
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
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|JcrConstants
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
name|Blob
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
name|PropertyState
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
name|Tree
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
name|Type
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
name|plugins
operator|.
name|index
operator|.
name|lucene
operator|.
name|util
operator|.
name|ConfigUtil
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
name|plugins
operator|.
name|index
operator|.
name|lucene
operator|.
name|util
operator|.
name|TokenizerChain
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
name|plugins
operator|.
name|tree
operator|.
name|factories
operator|.
name|TreeFactory
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
name|state
operator|.
name|NodeState
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
name|state
operator|.
name|NodeStateUtils
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
name|Analyzer
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
name|util
operator|.
name|AbstractAnalysisFactory
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
name|util
operator|.
name|CharArraySet
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
name|util
operator|.
name|CharFilterFactory
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
name|util
operator|.
name|ClasspathResourceLoader
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
name|util
operator|.
name|ResourceLoader
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
name|util
operator|.
name|ResourceLoaderAware
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
name|util
operator|.
name|StopwordAnalyzerBase
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
name|util
operator|.
name|TokenFilterFactory
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
name|util
operator|.
name|TokenizerFactory
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
name|util
operator|.
name|WordlistLoader
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
name|util
operator|.
name|IOUtils
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
name|util
operator|.
name|Version
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
name|Preconditions
operator|.
name|checkNotNull
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
name|Lists
operator|.
name|newArrayList
import|;
end_import

begin_comment
comment|/**  * Constructs the TokenizerChain based on NodeState content. Approach taken is similar  * to one taken in org.apache.solr.schema.FieldTypePluginLoader which is implemented for  * xml based config. Resource lookup are performed via binary property access  */
end_comment

begin_class
specifier|final
class|class
name|NodeStateAnalyzerFactory
block|{
specifier|private
specifier|static
specifier|final
name|AtomicBoolean
name|versionWarningAlreadyLogged
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|IGNORE_PROP_NAMES
init|=
name|ImmutableSet
operator|.
name|of
argument_list|(
name|LuceneIndexConstants
operator|.
name|ANL_CLASS
argument_list|,
name|LuceneIndexConstants
operator|.
name|ANL_NAME
argument_list|,
name|JcrConstants
operator|.
name|JCR_PRIMARYTYPE
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|NodeStateAnalyzerFactory
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|ResourceLoader
name|defaultLoader
decl_stmt|;
specifier|private
specifier|final
name|Version
name|defaultVersion
decl_stmt|;
specifier|public
name|NodeStateAnalyzerFactory
parameter_list|(
name|Version
name|defaultVersion
parameter_list|)
block|{
name|this
argument_list|(
operator|new
name|ClasspathResourceLoader
argument_list|(
name|NodeStateAnalyzerFactory
operator|.
name|class
operator|.
name|getClassLoader
argument_list|()
argument_list|)
argument_list|,
name|defaultVersion
argument_list|)
expr_stmt|;
block|}
specifier|public
name|NodeStateAnalyzerFactory
parameter_list|(
name|ResourceLoader
name|defaultLoader
parameter_list|,
name|Version
name|defaultVersion
parameter_list|)
block|{
name|this
operator|.
name|defaultLoader
operator|=
name|defaultLoader
expr_stmt|;
name|this
operator|.
name|defaultVersion
operator|=
name|defaultVersion
expr_stmt|;
block|}
specifier|public
name|Analyzer
name|createInstance
parameter_list|(
name|NodeState
name|state
parameter_list|)
block|{
if|if
condition|(
name|state
operator|.
name|hasProperty
argument_list|(
name|LuceneIndexConstants
operator|.
name|ANL_CLASS
argument_list|)
condition|)
block|{
return|return
name|createAnalyzerViaReflection
argument_list|(
name|state
argument_list|)
return|;
block|}
return|return
name|composeAnalyzer
argument_list|(
name|state
argument_list|)
return|;
block|}
specifier|private
name|Analyzer
name|composeAnalyzer
parameter_list|(
name|NodeState
name|state
parameter_list|)
block|{
name|TokenizerFactory
name|tf
init|=
name|loadTokenizer
argument_list|(
name|state
operator|.
name|getChildNode
argument_list|(
name|LuceneIndexConstants
operator|.
name|ANL_TOKENIZER
argument_list|)
argument_list|)
decl_stmt|;
name|CharFilterFactory
index|[]
name|cfs
init|=
name|loadCharFilterFactories
argument_list|(
name|state
operator|.
name|getChildNode
argument_list|(
name|LuceneIndexConstants
operator|.
name|ANL_CHAR_FILTERS
argument_list|)
argument_list|)
decl_stmt|;
name|TokenFilterFactory
index|[]
name|tffs
init|=
name|loadTokenFilterFactories
argument_list|(
name|state
operator|.
name|getChildNode
argument_list|(
name|LuceneIndexConstants
operator|.
name|ANL_FILTERS
argument_list|)
argument_list|)
decl_stmt|;
return|return
operator|new
name|TokenizerChain
argument_list|(
name|cfs
argument_list|,
name|tf
argument_list|,
name|tffs
argument_list|)
return|;
block|}
specifier|private
name|TokenFilterFactory
index|[]
name|loadTokenFilterFactories
parameter_list|(
name|NodeState
name|tokenFiltersState
parameter_list|)
block|{
name|List
argument_list|<
name|TokenFilterFactory
argument_list|>
name|result
init|=
name|newArrayList
argument_list|()
decl_stmt|;
name|Tree
name|tree
init|=
name|TreeFactory
operator|.
name|createReadOnlyTree
argument_list|(
name|tokenFiltersState
argument_list|)
decl_stmt|;
for|for
control|(
name|Tree
name|t
range|:
name|tree
operator|.
name|getChildren
argument_list|()
control|)
block|{
name|NodeState
name|state
init|=
name|tokenFiltersState
operator|.
name|getChildNode
argument_list|(
name|t
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|factoryType
init|=
name|getFactoryType
argument_list|(
name|state
argument_list|,
name|t
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
init|=
name|convertNodeState
argument_list|(
name|state
argument_list|)
decl_stmt|;
name|TokenFilterFactory
name|cf
init|=
name|TokenFilterFactory
operator|.
name|forName
argument_list|(
name|factoryType
argument_list|,
name|args
argument_list|)
decl_stmt|;
name|init
argument_list|(
name|cf
argument_list|,
name|state
argument_list|)
expr_stmt|;
name|result
operator|.
name|add
argument_list|(
name|cf
argument_list|)
expr_stmt|;
block|}
return|return
name|result
operator|.
name|toArray
argument_list|(
operator|new
name|TokenFilterFactory
index|[
name|result
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
specifier|private
name|CharFilterFactory
index|[]
name|loadCharFilterFactories
parameter_list|(
name|NodeState
name|charFiltersState
parameter_list|)
block|{
name|List
argument_list|<
name|CharFilterFactory
argument_list|>
name|result
init|=
name|newArrayList
argument_list|()
decl_stmt|;
comment|//Need to read children in order
name|Tree
name|tree
init|=
name|TreeFactory
operator|.
name|createReadOnlyTree
argument_list|(
name|charFiltersState
argument_list|)
decl_stmt|;
for|for
control|(
name|Tree
name|t
range|:
name|tree
operator|.
name|getChildren
argument_list|()
control|)
block|{
name|NodeState
name|state
init|=
name|charFiltersState
operator|.
name|getChildNode
argument_list|(
name|t
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|factoryType
init|=
name|getFactoryType
argument_list|(
name|state
argument_list|,
name|t
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
init|=
name|convertNodeState
argument_list|(
name|state
argument_list|)
decl_stmt|;
name|CharFilterFactory
name|cf
init|=
name|CharFilterFactory
operator|.
name|forName
argument_list|(
name|factoryType
argument_list|,
name|args
argument_list|)
decl_stmt|;
name|init
argument_list|(
name|cf
argument_list|,
name|state
argument_list|)
expr_stmt|;
name|result
operator|.
name|add
argument_list|(
name|cf
argument_list|)
expr_stmt|;
block|}
return|return
name|result
operator|.
name|toArray
argument_list|(
operator|new
name|CharFilterFactory
index|[
name|result
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
specifier|private
name|TokenizerFactory
name|loadTokenizer
parameter_list|(
name|NodeState
name|state
parameter_list|)
block|{
name|String
name|clazz
init|=
name|checkNotNull
argument_list|(
name|state
operator|.
name|getString
argument_list|(
name|LuceneIndexConstants
operator|.
name|ANL_NAME
argument_list|)
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
init|=
name|convertNodeState
argument_list|(
name|state
argument_list|)
decl_stmt|;
name|TokenizerFactory
name|tf
init|=
name|TokenizerFactory
operator|.
name|forName
argument_list|(
name|clazz
argument_list|,
name|args
argument_list|)
decl_stmt|;
name|init
argument_list|(
name|tf
argument_list|,
name|state
argument_list|)
expr_stmt|;
return|return
name|tf
return|;
block|}
specifier|private
name|Analyzer
name|createAnalyzerViaReflection
parameter_list|(
name|NodeState
name|state
parameter_list|)
block|{
name|String
name|clazz
init|=
name|state
operator|.
name|getString
argument_list|(
name|LuceneIndexConstants
operator|.
name|ANL_CLASS
argument_list|)
decl_stmt|;
name|Class
argument_list|<
name|?
extends|extends
name|Analyzer
argument_list|>
name|analyzerClazz
init|=
name|defaultLoader
operator|.
name|findClass
argument_list|(
name|clazz
argument_list|,
name|Analyzer
operator|.
name|class
argument_list|)
decl_stmt|;
name|Version
name|matchVersion
init|=
name|getVersion
argument_list|(
name|state
argument_list|)
decl_stmt|;
name|CharArraySet
name|stopwords
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|StopwordAnalyzerBase
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|analyzerClazz
argument_list|)
operator|&&
name|state
operator|.
name|hasChildNode
argument_list|(
name|LuceneIndexConstants
operator|.
name|ANL_STOPWORDS
argument_list|)
condition|)
block|{
try|try
block|{
name|stopwords
operator|=
name|loadStopwordSet
argument_list|(
name|state
operator|.
name|getChildNode
argument_list|(
name|LuceneIndexConstants
operator|.
name|ANL_STOPWORDS
argument_list|)
argument_list|,
name|LuceneIndexConstants
operator|.
name|ANL_STOPWORDS
argument_list|,
name|matchVersion
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Error occurred while loading stopwords"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
name|Constructor
argument_list|<
name|?
extends|extends
name|Analyzer
argument_list|>
name|c
init|=
literal|null
decl_stmt|;
try|try
block|{
if|if
condition|(
name|stopwords
operator|!=
literal|null
condition|)
block|{
name|c
operator|=
name|analyzerClazz
operator|.
name|getConstructor
argument_list|(
name|Version
operator|.
name|class
argument_list|,
name|CharArraySet
operator|.
name|class
argument_list|)
expr_stmt|;
return|return
name|c
operator|.
name|newInstance
argument_list|(
name|matchVersion
argument_list|,
name|stopwords
argument_list|)
return|;
block|}
else|else
block|{
name|c
operator|=
name|analyzerClazz
operator|.
name|getConstructor
argument_list|(
name|Version
operator|.
name|class
argument_list|)
expr_stmt|;
return|return
name|c
operator|.
name|newInstance
argument_list|(
name|matchVersion
argument_list|)
return|;
block|}
block|}
catch|catch
parameter_list|(
name|NoSuchMethodException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Error occurred while instantiating Analyzer for "
operator|+
name|analyzerClazz
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|InstantiationException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Error occurred while instantiating Analyzer for "
operator|+
name|analyzerClazz
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IllegalAccessException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Error occurred while instantiating Analyzer for "
operator|+
name|analyzerClazz
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|InvocationTargetException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Error occurred while instantiating Analyzer for "
operator|+
name|analyzerClazz
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
specifier|private
name|void
name|init
parameter_list|(
name|AbstractAnalysisFactory
name|o
parameter_list|,
name|NodeState
name|state
parameter_list|)
block|{
if|if
condition|(
name|o
operator|instanceof
name|ResourceLoaderAware
condition|)
block|{
try|try
block|{
operator|(
operator|(
name|ResourceLoaderAware
operator|)
name|o
operator|)
operator|.
name|inform
argument_list|(
operator|new
name|NodeStateResourceLoader
argument_list|(
name|state
argument_list|,
name|defaultLoader
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Error occurred while initializing type "
operator|+
name|o
operator|.
name|getClass
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|state
operator|.
name|hasProperty
argument_list|(
name|LuceneIndexConstants
operator|.
name|ANL_LUCENE_MATCH_VERSION
argument_list|)
condition|)
block|{
name|o
operator|.
name|setExplicitLuceneMatchVersion
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|convertNodeState
parameter_list|(
name|NodeState
name|state
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|result
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
for|for
control|(
name|PropertyState
name|ps
range|:
name|state
operator|.
name|getProperties
argument_list|()
control|)
block|{
name|String
name|name
init|=
name|ps
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
name|ps
operator|.
name|getType
argument_list|()
operator|!=
name|Type
operator|.
name|BINARY
operator|&&
operator|!
name|ps
operator|.
name|isArray
argument_list|()
operator|&&
operator|!
operator|(
name|name
operator|!=
literal|null
operator|&&
name|NodeStateUtils
operator|.
name|isHidden
argument_list|(
name|name
argument_list|)
operator|)
operator|&&
operator|!
name|IGNORE_PROP_NAMES
operator|.
name|contains
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|result
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|ps
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRING
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|result
operator|.
name|put
argument_list|(
name|LuceneIndexConstants
operator|.
name|ANL_LUCENE_MATCH_VERSION
argument_list|,
name|getVersion
argument_list|(
name|state
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
specifier|private
name|Version
name|getVersion
parameter_list|(
name|NodeState
name|state
parameter_list|)
block|{
name|Version
name|version
init|=
name|defaultVersion
decl_stmt|;
if|if
condition|(
name|state
operator|.
name|hasProperty
argument_list|(
name|LuceneIndexConstants
operator|.
name|ANL_LUCENE_MATCH_VERSION
argument_list|)
condition|)
block|{
name|version
operator|=
name|parseLuceneVersionString
argument_list|(
name|state
operator|.
name|getString
argument_list|(
name|LuceneIndexConstants
operator|.
name|ANL_LUCENE_MATCH_VERSION
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|version
return|;
block|}
specifier|private
specifier|static
name|String
name|getFactoryType
parameter_list|(
name|NodeState
name|state
parameter_list|,
name|String
name|nodeStateName
parameter_list|)
block|{
name|String
name|type
init|=
name|state
operator|.
name|getString
argument_list|(
name|LuceneIndexConstants
operator|.
name|ANL_NAME
argument_list|)
decl_stmt|;
return|return
name|type
operator|!=
literal|null
condition|?
name|type
else|:
name|nodeStateName
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
specifier|private
specifier|static
name|Version
name|parseLuceneVersionString
parameter_list|(
specifier|final
name|String
name|matchVersion
parameter_list|)
block|{
specifier|final
name|Version
name|version
init|=
name|Version
operator|.
name|parseLeniently
argument_list|(
name|matchVersion
argument_list|)
decl_stmt|;
if|if
condition|(
name|version
operator|==
name|Version
operator|.
name|LUCENE_CURRENT
operator|&&
operator|!
name|versionWarningAlreadyLogged
operator|.
name|getAndSet
argument_list|(
literal|true
argument_list|)
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"You should not use LATEST as luceneMatchVersion property: "
operator|+
literal|"if you use this setting, and then Solr upgrades to a newer release of Lucene, "
operator|+
literal|"sizable changes may happen. If precise back compatibility is important "
operator|+
literal|"then you should instead explicitly specify an actual Lucene version."
argument_list|)
expr_stmt|;
block|}
return|return
name|version
return|;
block|}
specifier|private
specifier|static
name|CharArraySet
name|loadStopwordSet
parameter_list|(
name|NodeState
name|file
parameter_list|,
name|String
name|name
parameter_list|,
name|Version
name|matchVersion
parameter_list|)
throws|throws
name|IOException
block|{
name|Blob
name|blob
init|=
name|ConfigUtil
operator|.
name|getBlob
argument_list|(
name|file
argument_list|,
name|name
argument_list|)
decl_stmt|;
name|Reader
name|stopwords
init|=
operator|new
name|InputStreamReader
argument_list|(
name|blob
operator|.
name|getNewStream
argument_list|()
argument_list|,
name|IOUtils
operator|.
name|CHARSET_UTF_8
argument_list|)
decl_stmt|;
try|try
block|{
return|return
name|WordlistLoader
operator|.
name|getWordSet
argument_list|(
name|stopwords
argument_list|,
name|matchVersion
argument_list|)
return|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|stopwords
argument_list|)
expr_stmt|;
block|}
block|}
specifier|static
class|class
name|NodeStateResourceLoader
implements|implements
name|ResourceLoader
block|{
specifier|private
specifier|final
name|NodeState
name|state
decl_stmt|;
specifier|private
specifier|final
name|ResourceLoader
name|delegate
decl_stmt|;
specifier|public
name|NodeStateResourceLoader
parameter_list|(
name|NodeState
name|state
parameter_list|,
name|ResourceLoader
name|delegate
parameter_list|)
block|{
name|this
operator|.
name|state
operator|=
name|state
expr_stmt|;
name|this
operator|.
name|delegate
operator|=
name|delegate
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|InputStream
name|openResource
parameter_list|(
name|String
name|resource
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|state
operator|.
name|hasChildNode
argument_list|(
name|resource
argument_list|)
condition|)
block|{
return|return
name|ConfigUtil
operator|.
name|getBlob
argument_list|(
name|state
operator|.
name|getChildNode
argument_list|(
name|resource
argument_list|)
argument_list|,
name|resource
argument_list|)
operator|.
name|getNewStream
argument_list|()
return|;
block|}
return|return
name|delegate
operator|.
name|openResource
argument_list|(
name|resource
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
parameter_list|<
name|T
parameter_list|>
name|Class
argument_list|<
name|?
extends|extends
name|T
argument_list|>
name|findClass
parameter_list|(
name|String
name|cname
parameter_list|,
name|Class
argument_list|<
name|T
argument_list|>
name|expectedType
parameter_list|)
block|{
comment|//For factories the cname is not FQN. Instead its the name without suffix
comment|//For e.g. for WhitespaceTokenizerFactory its 'whitespace'
if|if
condition|(
name|CharFilterFactory
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|expectedType
argument_list|)
condition|)
block|{
return|return
name|CharFilterFactory
operator|.
name|lookupClass
argument_list|(
name|cname
argument_list|)
operator|.
name|asSubclass
argument_list|(
name|expectedType
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|TokenizerFactory
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|expectedType
argument_list|)
condition|)
block|{
return|return
name|TokenizerFactory
operator|.
name|lookupClass
argument_list|(
name|cname
argument_list|)
operator|.
name|asSubclass
argument_list|(
name|expectedType
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|TokenFilterFactory
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|expectedType
argument_list|)
condition|)
block|{
return|return
name|TokenFilterFactory
operator|.
name|lookupClass
argument_list|(
name|cname
argument_list|)
operator|.
name|asSubclass
argument_list|(
name|expectedType
argument_list|)
return|;
block|}
return|return
name|delegate
operator|.
name|findClass
argument_list|(
name|cname
argument_list|,
name|expectedType
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
parameter_list|<
name|T
parameter_list|>
name|T
name|newInstance
parameter_list|(
name|String
name|cname
parameter_list|,
name|Class
argument_list|<
name|T
argument_list|>
name|expectedType
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
block|}
end_class

end_unit

