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
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|mock
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

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
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|FileUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|lang3
operator|.
name|reflect
operator|.
name|FieldUtils
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
name|jmx
operator|.
name|CacheStatsMBean
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
name|blob
operator|.
name|datastore
operator|.
name|CachingFileDataStore
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
name|blob
operator|.
name|datastore
operator|.
name|DataStoreBlobStore
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
name|blob
operator|.
name|datastore
operator|.
name|DataStoreUtils
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
name|document
operator|.
name|spi
operator|.
name|JournalPropertyService
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
name|IndexEditorProvider
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
name|IndexPathService
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
name|fulltext
operator|.
name|ExtractedText
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
name|fulltext
operator|.
name|PreExtractedTextProvider
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
name|score
operator|.
name|ScorerProviderFactory
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
name|memory
operator|.
name|MemoryNodeStore
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
name|BackgroundObserver
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
name|Observer
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
name|mount
operator|.
name|MountInfoProvider
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
name|mount
operator|.
name|Mounts
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
name|query
operator|.
name|QueryIndexProvider
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
name|NodeStore
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
name|stats
operator|.
name|StatisticsProvider
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
name|search
operator|.
name|BooleanQuery
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
name|InfoStream
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|sling
operator|.
name|testing
operator|.
name|mock
operator|.
name|osgi
operator|.
name|MockOsgi
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|sling
operator|.
name|testing
operator|.
name|mock
operator|.
name|osgi
operator|.
name|junit
operator|.
name|OsgiContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
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
name|Rule
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
import|import
name|org
operator|.
name|junit
operator|.
name|rules
operator|.
name|TemporaryFolder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|osgi
operator|.
name|framework
operator|.
name|ServiceReference
import|;
end_import

begin_class
specifier|public
class|class
name|LuceneIndexProviderServiceTest
block|{
comment|/*         The test case uses raw config name and not access it via          constants in LuceneIndexProviderService to ensure that change          in names are detected      */
annotation|@
name|Rule
specifier|public
specifier|final
name|TemporaryFolder
name|folder
init|=
operator|new
name|TemporaryFolder
argument_list|(
operator|new
name|File
argument_list|(
literal|"target"
argument_list|)
argument_list|)
decl_stmt|;
annotation|@
name|Rule
specifier|public
specifier|final
name|OsgiContext
name|context
init|=
operator|new
name|OsgiContext
argument_list|()
decl_stmt|;
specifier|private
name|LuceneIndexProviderService
name|service
init|=
operator|new
name|LuceneIndexProviderService
argument_list|()
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
block|{
name|context
operator|.
name|registerService
argument_list|(
name|MountInfoProvider
operator|.
name|class
argument_list|,
name|Mounts
operator|.
name|defaultMountInfoProvider
argument_list|()
argument_list|)
expr_stmt|;
name|context
operator|.
name|registerService
argument_list|(
name|StatisticsProvider
operator|.
name|class
argument_list|,
name|StatisticsProvider
operator|.
name|NOOP
argument_list|)
expr_stmt|;
name|context
operator|.
name|registerService
argument_list|(
name|ScorerProviderFactory
operator|.
name|class
argument_list|,
name|ScorerProviderFactory
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
name|context
operator|.
name|registerService
argument_list|(
name|IndexAugmentorFactory
operator|.
name|class
argument_list|,
name|mock
argument_list|(
name|IndexAugmentorFactory
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|context
operator|.
name|registerService
argument_list|(
name|NodeStore
operator|.
name|class
argument_list|,
operator|new
name|MemoryNodeStore
argument_list|()
argument_list|)
expr_stmt|;
name|context
operator|.
name|registerService
argument_list|(
name|IndexPathService
operator|.
name|class
argument_list|,
name|mock
argument_list|(
name|IndexPathService
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|MockOsgi
operator|.
name|injectServices
argument_list|(
name|service
argument_list|,
name|context
operator|.
name|bundleContext
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|after
parameter_list|()
block|{
name|IndexDefinition
operator|.
name|setDisableStoredIndexDefinition
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|defaultSetup
parameter_list|()
throws|throws
name|Exception
block|{
name|MockOsgi
operator|.
name|activate
argument_list|(
name|service
argument_list|,
name|context
operator|.
name|bundleContext
argument_list|()
argument_list|,
name|getDefaultConfig
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|context
operator|.
name|getService
argument_list|(
name|QueryIndexProvider
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|context
operator|.
name|getService
argument_list|(
name|Observer
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|context
operator|.
name|getService
argument_list|(
name|IndexEditorProvider
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|LuceneIndexEditorProvider
name|editorProvider
init|=
operator|(
name|LuceneIndexEditorProvider
operator|)
name|context
operator|.
name|getService
argument_list|(
name|IndexEditorProvider
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|editorProvider
operator|.
name|getIndexCopier
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|editorProvider
operator|.
name|getIndexingQueue
argument_list|()
argument_list|)
expr_stmt|;
name|IndexCopier
name|indexCopier
init|=
name|service
operator|.
name|getIndexCopier
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"IndexCopier should be initialized as CopyOnRead is enabled by default"
argument_list|,
name|indexCopier
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|indexCopier
operator|.
name|isPrefetchEnabled
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|IndexDefinition
operator|.
name|isDisableStoredIndexDefinition
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"CopyOnRead should be enabled by default"
argument_list|,
name|context
operator|.
name|getService
argument_list|(
name|CopyOnReadStatsMBean
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|context
operator|.
name|getService
argument_list|(
name|CacheStatsMBean
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|context
operator|.
name|getService
argument_list|(
name|Observer
operator|.
name|class
argument_list|)
operator|instanceof
name|BackgroundObserver
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|InfoStream
operator|.
name|NO_OUTPUT
argument_list|,
name|InfoStream
operator|.
name|getDefault
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1024
argument_list|,
name|BooleanQuery
operator|.
name|getMaxClauseCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|FieldUtils
operator|.
name|readDeclaredField
argument_list|(
name|service
argument_list|,
literal|"documentQueue"
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|context
operator|.
name|getService
argument_list|(
name|JournalPropertyService
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|MockOsgi
operator|.
name|deactivate
argument_list|(
name|service
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|typeProperty
parameter_list|()
throws|throws
name|Exception
block|{
name|MockOsgi
operator|.
name|activate
argument_list|(
name|service
argument_list|,
name|context
operator|.
name|bundleContext
argument_list|()
argument_list|,
name|getDefaultConfig
argument_list|()
argument_list|)
expr_stmt|;
name|ServiceReference
name|sr
init|=
name|context
operator|.
name|bundleContext
argument_list|()
operator|.
name|getServiceReference
argument_list|(
name|IndexEditorProvider
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"lucene"
argument_list|,
name|sr
operator|.
name|getProperty
argument_list|(
literal|"type"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|disableOpenIndexAsync
parameter_list|()
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|config
init|=
name|getDefaultConfig
argument_list|()
decl_stmt|;
name|config
operator|.
name|put
argument_list|(
literal|"enableOpenIndexAsync"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|MockOsgi
operator|.
name|activate
argument_list|(
name|service
argument_list|,
name|context
operator|.
name|bundleContext
argument_list|()
argument_list|,
name|config
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|context
operator|.
name|getService
argument_list|(
name|Observer
operator|.
name|class
argument_list|)
operator|instanceof
name|LuceneIndexProvider
argument_list|)
expr_stmt|;
name|MockOsgi
operator|.
name|deactivate
argument_list|(
name|service
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|enableCopyOnWrite
parameter_list|()
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|config
init|=
name|getDefaultConfig
argument_list|()
decl_stmt|;
name|config
operator|.
name|put
argument_list|(
literal|"enableCopyOnWriteSupport"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|MockOsgi
operator|.
name|activate
argument_list|(
name|service
argument_list|,
name|context
operator|.
name|bundleContext
argument_list|()
argument_list|,
name|config
argument_list|)
expr_stmt|;
name|LuceneIndexEditorProvider
name|editorProvider
init|=
operator|(
name|LuceneIndexEditorProvider
operator|)
name|context
operator|.
name|getService
argument_list|(
name|IndexEditorProvider
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|editorProvider
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|editorProvider
operator|.
name|getIndexCopier
argument_list|()
argument_list|)
expr_stmt|;
name|MockOsgi
operator|.
name|deactivate
argument_list|(
name|service
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|enablePrefetchIndexFiles
parameter_list|()
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|config
init|=
name|getDefaultConfig
argument_list|()
decl_stmt|;
name|config
operator|.
name|put
argument_list|(
literal|"prefetchIndexFiles"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|MockOsgi
operator|.
name|activate
argument_list|(
name|service
argument_list|,
name|context
operator|.
name|bundleContext
argument_list|()
argument_list|,
name|config
argument_list|)
expr_stmt|;
name|IndexCopier
name|indexCopier
init|=
name|service
operator|.
name|getIndexCopier
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|indexCopier
operator|.
name|isPrefetchEnabled
argument_list|()
argument_list|)
expr_stmt|;
name|MockOsgi
operator|.
name|deactivate
argument_list|(
name|service
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|debugLogging
parameter_list|()
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|config
init|=
name|getDefaultConfig
argument_list|()
decl_stmt|;
name|config
operator|.
name|put
argument_list|(
literal|"debug"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|MockOsgi
operator|.
name|activate
argument_list|(
name|service
argument_list|,
name|context
operator|.
name|bundleContext
argument_list|()
argument_list|,
name|config
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|LoggingInfoStream
operator|.
name|INSTANCE
argument_list|,
name|InfoStream
operator|.
name|getDefault
argument_list|()
argument_list|)
expr_stmt|;
name|MockOsgi
operator|.
name|deactivate
argument_list|(
name|service
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|enableExtractedTextCaching
parameter_list|()
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|config
init|=
name|getDefaultConfig
argument_list|()
decl_stmt|;
name|config
operator|.
name|put
argument_list|(
literal|"extractedTextCacheSizeInMB"
argument_list|,
literal|11
argument_list|)
expr_stmt|;
name|MockOsgi
operator|.
name|activate
argument_list|(
name|service
argument_list|,
name|context
operator|.
name|bundleContext
argument_list|()
argument_list|,
name|config
argument_list|)
expr_stmt|;
name|ExtractedTextCache
name|textCache
init|=
name|service
operator|.
name|getExtractedTextCache
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|textCache
operator|.
name|getCacheStats
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|context
operator|.
name|getService
argument_list|(
name|CacheStatsMBean
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|11
operator|*
name|FileUtils
operator|.
name|ONE_MB
argument_list|,
name|textCache
operator|.
name|getCacheStats
argument_list|()
operator|.
name|getMaxTotalWeight
argument_list|()
argument_list|)
expr_stmt|;
name|MockOsgi
operator|.
name|deactivate
argument_list|(
name|service
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|context
operator|.
name|getService
argument_list|(
name|CacheStatsMBean
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
name|preExtractedTextProvider
parameter_list|()
throws|throws
name|Exception
block|{
name|MockOsgi
operator|.
name|activate
argument_list|(
name|service
argument_list|,
name|context
operator|.
name|bundleContext
argument_list|()
argument_list|,
name|getDefaultConfig
argument_list|()
argument_list|)
expr_stmt|;
name|LuceneIndexEditorProvider
name|editorProvider
init|=
operator|(
name|LuceneIndexEditorProvider
operator|)
name|context
operator|.
name|getService
argument_list|(
name|IndexEditorProvider
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|editorProvider
operator|.
name|getExtractedTextCache
argument_list|()
operator|.
name|getExtractedTextProvider
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|editorProvider
operator|.
name|getExtractedTextCache
argument_list|()
operator|.
name|isAlwaysUsePreExtractedCache
argument_list|()
argument_list|)
expr_stmt|;
comment|//Mock OSGi does not support components
comment|//context.registerService(PreExtractedTextProvider.class, new DummyProvider());
name|service
operator|.
name|bindExtractedTextProvider
argument_list|(
operator|new
name|DummyProvider
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|editorProvider
operator|.
name|getExtractedTextCache
argument_list|()
operator|.
name|getExtractedTextProvider
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|preExtractedProviderBindBeforeActivate
parameter_list|()
throws|throws
name|Exception
block|{
name|service
operator|.
name|bindExtractedTextProvider
argument_list|(
operator|new
name|DummyProvider
argument_list|()
argument_list|)
expr_stmt|;
name|MockOsgi
operator|.
name|activate
argument_list|(
name|service
argument_list|,
name|context
operator|.
name|bundleContext
argument_list|()
argument_list|,
name|getDefaultConfig
argument_list|()
argument_list|)
expr_stmt|;
name|LuceneIndexEditorProvider
name|editorProvider
init|=
operator|(
name|LuceneIndexEditorProvider
operator|)
name|context
operator|.
name|getService
argument_list|(
name|IndexEditorProvider
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|editorProvider
operator|.
name|getExtractedTextCache
argument_list|()
operator|.
name|getExtractedTextProvider
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|alwaysUsePreExtractedCache
parameter_list|()
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|config
init|=
name|getDefaultConfig
argument_list|()
decl_stmt|;
name|config
operator|.
name|put
argument_list|(
literal|"alwaysUsePreExtractedCache"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|MockOsgi
operator|.
name|activate
argument_list|(
name|service
argument_list|,
name|context
operator|.
name|bundleContext
argument_list|()
argument_list|,
name|config
argument_list|)
expr_stmt|;
name|LuceneIndexEditorProvider
name|editorProvider
init|=
operator|(
name|LuceneIndexEditorProvider
operator|)
name|context
operator|.
name|getService
argument_list|(
name|IndexEditorProvider
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|editorProvider
operator|.
name|getExtractedTextCache
argument_list|()
operator|.
name|isAlwaysUsePreExtractedCache
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|booleanQuerySize
parameter_list|()
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|config
init|=
name|getDefaultConfig
argument_list|()
decl_stmt|;
name|config
operator|.
name|put
argument_list|(
literal|"booleanClauseLimit"
argument_list|,
literal|4000
argument_list|)
expr_stmt|;
name|MockOsgi
operator|.
name|activate
argument_list|(
name|service
argument_list|,
name|context
operator|.
name|bundleContext
argument_list|()
argument_list|,
name|config
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4000
argument_list|,
name|BooleanQuery
operator|.
name|getMaxClauseCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|indexDefnStorafe
parameter_list|()
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|config
init|=
name|getDefaultConfig
argument_list|()
decl_stmt|;
name|config
operator|.
name|put
argument_list|(
literal|"disableStoredIndexDefinition"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|MockOsgi
operator|.
name|activate
argument_list|(
name|service
argument_list|,
name|context
operator|.
name|bundleContext
argument_list|()
argument_list|,
name|config
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|IndexDefinition
operator|.
name|isDisableStoredIndexDefinition
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|blobStoreRegistered
parameter_list|()
throws|throws
name|Exception
block|{
name|MockOsgi
operator|.
name|activate
argument_list|(
name|service
argument_list|,
name|context
operator|.
name|bundleContext
argument_list|()
argument_list|,
name|getDefaultConfig
argument_list|()
argument_list|)
expr_stmt|;
name|LuceneIndexEditorProvider
name|editorProvider
init|=
operator|(
name|LuceneIndexEditorProvider
operator|)
name|context
operator|.
name|getService
argument_list|(
name|IndexEditorProvider
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|editorProvider
operator|.
name|getBlobStore
argument_list|()
argument_list|)
expr_stmt|;
comment|/* Register a blob store */
name|CachingFileDataStore
name|ds
init|=
name|DataStoreUtils
operator|.
name|createCachingFDS
argument_list|(
name|folder
operator|.
name|newFolder
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|folder
operator|.
name|newFolder
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
decl_stmt|;
name|service
operator|.
name|bindBlobStore
argument_list|(
operator|new
name|DataStoreBlobStore
argument_list|(
name|ds
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|editorProvider
operator|.
name|getBlobStore
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|getDefaultConfig
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|config
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
name|config
operator|.
name|put
argument_list|(
literal|"localIndexDir"
argument_list|,
name|folder
operator|.
name|getRoot
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|config
return|;
block|}
specifier|private
specifier|static
class|class
name|DummyProvider
implements|implements
name|PreExtractedTextProvider
block|{
annotation|@
name|Override
specifier|public
name|ExtractedText
name|getText
parameter_list|(
name|String
name|propertyPath
parameter_list|,
name|Blob
name|blob
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|null
return|;
block|}
block|}
block|}
end_class

end_unit

