import { useState, useEffect, useCallback } from 'react';
import { AuthProvider, useAuth } from './context/AuthContext';
import api from './services/api';
import Navbar from './components/Navbar';
import StatsCards from './components/StatsCards';
import AiSearchBar from './components/AiSearchBar';
import DocumentCard from './components/DocumentCard';
import UploadModal from './components/UploadModal';
import DocumentDetailModal from './components/DocumentDetailModal';
import ShareModal from './components/ShareModal';
import AuthView from './components/AuthView';
import PublicShareView from './components/PublicShareView';
import {
  UploadCloud,
  FileQuestion,
  Layers,
  Sparkles,
  RefreshCw,
  FolderOpen
} from 'lucide-react';

const CATEGORIES = [
  { id: 'ALL', label: 'All Documents' },
  { id: 'INVOICE', label: 'Invoices' },
  { id: 'RESUME', label: 'Resumes' },
  { id: 'ID_CARD', label: 'ID Cards' },
  { id: 'CONTRACT', label: 'Contracts' },
  { id: 'ACADEMIC', label: 'Academic' },
  { id: 'MEDICAL', label: 'Medical' },
  { id: 'FINANCIAL', label: 'Financial' },
  { id: 'REPORT', label: 'Reports' },
  { id: 'OTHER', label: 'Other' }
];

function Dashboard() {
  const { user } = useAuth();
  const [stats, setStats] = useState(null);
  const [documents, setDocuments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [selectedCategory, setSelectedCategory] = useState('ALL');
  const [searchQuery, setSearchQuery] = useState('');
  const [aiStatus, setAiStatus] = useState(null);

  // Modals state
  const [isUploadOpen, setIsUploadOpen] = useState(false);
  const [viewDoc, setViewDoc] = useState(null);
  const [shareDoc, setShareDoc] = useState(null);

  const fetchStats = async () => {
    try {
      const res = await api.get('/documents/stats');
      setStats(res.data);
    } catch (e) {
      console.error(e);
    }
  };

  const fetchAiStatus = async () => {
    try {
      const res = await api.get('/ai/status');
      setAiStatus(res.data);
    } catch (e) {
      console.error(e);
    }
  };

  const fetchDocuments = useCallback(async () => {
    setLoading(true);
    try {
      const params = {};
      if (selectedCategory !== 'ALL') {
        params.category = selectedCategory;
      }
      if (searchQuery) {
        params.query = searchQuery;
      }
      const res = await api.get('/documents', { params });
      setDocuments(res.data);
    } catch (e) {
      console.error(e);
    } finally {
      setLoading(false);
    }
  }, [selectedCategory, searchQuery]);

  useEffect(() => {
    fetchStats();
    fetchAiStatus();
  }, []);

  useEffect(() => {
    fetchDocuments();
  }, [fetchDocuments]);

  const handleDownload = (doc) => {
    window.open(`/api/documents/${doc.id}/file?download=true`, '_blank');
  };

  const handleDelete = async (doc) => {
    if (window.confirm(`Are you sure you want to delete "${doc.title}"?`)) {
      try {
        await api.delete(`/documents/${doc.id}`);
        setDocuments(documents.filter((d) => d.id !== doc.id));
        fetchStats();
      } catch (err) {
        alert('Failed to delete document: ' + (err.response?.data?.message || err.message));
      }
    }
  };

  const handleUploadSuccess = (newDoc) => {
    setDocuments([newDoc, ...documents]);
    fetchStats();
  };

  const handleDocumentUpdated = (updatedDoc) => {
    setDocuments(documents.map((d) => (d.id === updatedDoc.id ? updatedDoc : d)));
    fetchStats();
  };

  return (
    <div className="min-h-screen bg-slate-50 text-slate-800">
      <Navbar onOpenUpload={() => setIsUploadOpen(true)} aiStatus={aiStatus} />

      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-7">
        {/* Top Metric Cards */}
        <StatsCards stats={stats} />

        {/* AI Natural Language Search & Keyword Filter */}
        <AiSearchBar
          onFilterChange={(val) => setSearchQuery(val)}
          onAiSearchResult={(res) => {
            if (res && res.matchedDocuments) {
              setDocuments(res.matchedDocuments);
            } else if (!res) {
              fetchDocuments();
            }
          }}
        />

        {/* Category Pills & Controls */}
        <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4 pt-1">
          <div className="flex items-center space-x-1.5 overflow-x-auto pb-1 max-w-full no-scrollbar">
            {CATEGORIES.map((cat) => (
              <button
                key={cat.id}
                onClick={() => setSelectedCategory(cat.id)}
                className={`px-3 py-1.5 rounded-xl text-xs font-semibold whitespace-nowrap transition-all cursor-pointer ${
                  selectedCategory === cat.id
                    ? 'bg-slate-900 text-white shadow-xs'
                    : 'bg-white text-slate-600 hover:bg-slate-100 border border-slate-200'
                }`}
              >
                {cat.label}
              </button>
            ))}
          </div>

          <button
            onClick={fetchDocuments}
            title="Refresh List"
            className="p-2 bg-white border border-slate-200 text-slate-500 hover:text-slate-700 hover:bg-slate-100 rounded-xl transition-colors cursor-pointer self-end sm:self-auto shrink-0"
          >
            <RefreshCw className={`w-3.5 h-3.5 ${loading ? 'animate-spin' : ''}`} />
          </button>
        </div>

        {/* Documents Grid / List */}
        <div>
          {loading ? (
            <div className="py-20 text-center space-y-3">
              <div className="w-8 h-8 mx-auto border-3 border-indigo-600 border-t-transparent rounded-full animate-spin"></div>
              <p className="text-xs font-semibold text-slate-500">Loading documents from your locker...</p>
            </div>
          ) : documents.length === 0 ? (
            <div className="bg-white rounded-3xl border border-slate-200 p-12 text-center max-w-md mx-auto space-y-4 shadow-xs">
              <div className="w-14 h-14 rounded-2xl bg-indigo-50 text-indigo-600 mx-auto flex items-center justify-center">
                <FolderOpen className="w-7 h-7" />
              </div>
              <div>
                <h4 className="text-base font-bold text-slate-900">No documents found</h4>
                <p className="text-xs text-slate-500 mt-1">
                  {searchQuery || selectedCategory !== 'ALL'
                    ? 'No files matched your search or category filter.'
                    : 'Your personal document locker is currently empty. Upload your first PDF, receipt, or resume.'}
                </p>
              </div>
              <button
                onClick={() => setIsUploadOpen(true)}
                className="inline-flex items-center gap-2 px-4 py-2.5 text-xs font-semibold text-white bg-indigo-600 hover:bg-indigo-700 rounded-xl shadow-xs shadow-indigo-200 transition-all cursor-pointer mx-auto"
              >
                <UploadCloud className="w-4 h-4" />
                <span>Upload Document</span>
              </button>
            </div>
          ) : (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
              {documents.map((doc) => (
                <DocumentCard
                  key={doc.id}
                  doc={doc}
                  onView={(d) => setViewDoc(d)}
                  onShare={(d) => setShareDoc(d)}
                  onDownload={(d) => handleDownload(d)}
                  onDelete={(d) => handleDelete(d)}
                />
              ))}
            </div>
          )}
        </div>
      </main>

      {/* Upload Modal */}
      <UploadModal
        isOpen={isUploadOpen}
        onClose={() => setIsUploadOpen(false)}
        onUploadSuccess={handleUploadSuccess}
      />

      {/* Document Detail & AI Modal */}
      <DocumentDetailModal
        doc={viewDoc}
        isOpen={!!viewDoc}
        onClose={() => setViewDoc(null)}
        onShare={(d) => setShareDoc(d)}
        onDownload={(d) => handleDownload(d)}
        onDocumentUpdated={handleDocumentUpdated}
      />

      {/* Share Modal */}
      <ShareModal
        doc={shareDoc}
        isOpen={!!shareDoc}
        onClose={() => setShareDoc(null)}
        onShareUpdated={fetchStats}
      />
    </div>
  );
}

function MainApp() {
  const { isAuthenticated, loading } = useAuth();
  const [shareToken, setShareToken] = useState(null);

  useEffect(() => {
    const params = new URLSearchParams(window.location.search);
    const token = params.get('share');
    if (token) {
      setShareToken(token);
    }
  }, []);

  if (shareToken) {
    return (
      <PublicShareView
        shareToken={shareToken}
        onBackToApp={() => {
          window.history.pushState({}, '', window.location.pathname);
          setShareToken(null);
        }}
      />
    );
  }

  if (loading) {
    return (
      <div className="min-h-screen bg-slate-50 flex items-center justify-center p-4">
        <div className="w-8 h-8 border-3 border-indigo-600 border-t-transparent rounded-full animate-spin"></div>
      </div>
    );
  }

  return isAuthenticated ? <Dashboard /> : <AuthView />;
}

export default function App() {
  return (
    <AuthProvider>
      <MainApp />
    </AuthProvider>
  );
}
