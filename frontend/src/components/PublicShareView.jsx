import { useState, useEffect } from 'react';
import { ShieldCheck, Download, Sparkles, FileText, Calendar, Lock, AlertCircle } from 'lucide-react';
import axios from 'axios';

export default function PublicShareView({ shareToken, onBackToApp }) {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchSharedDoc = async () => {
      try {
        const res = await axios.get(`/api/share/access/${shareToken}`);
        setData(res.data);
      } catch (err) {
        setError(err.response?.data?.message || 'Link is invalid or has expired.');
      } finally {
        setLoading(false);
      }
    };

    fetchSharedDoc();
  }, [shareToken]);

  const handleDownload = () => {
    window.open(`/api/share/access/${shareToken}/file?download=true`, '_blank');
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-slate-50 flex items-center justify-center p-4">
        <div className="text-center space-y-3">
          <div className="w-10 h-10 mx-auto border-3 border-indigo-600 border-t-transparent rounded-full animate-spin"></div>
          <p className="text-xs font-semibold text-slate-600">Verifying secure share token...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="min-h-screen bg-slate-50 flex items-center justify-center p-4">
        <div className="bg-white rounded-2xl max-w-md w-full p-8 border border-slate-200 shadow-xl text-center space-y-4">
          <div className="w-12 h-12 rounded-full bg-rose-50 text-rose-600 mx-auto flex items-center justify-center">
            <AlertCircle className="w-6 h-6" />
          </div>
          <h3 className="text-lg font-bold text-slate-900">Access Restricted</h3>
          <p className="text-xs text-slate-500">{error}</p>
          <button
            onClick={onBackToApp}
            className="w-full py-2.5 text-xs font-semibold text-white bg-indigo-600 hover:bg-indigo-700 rounded-lg transition-colors cursor-pointer"
          >
            Go to Document Locker
          </button>
        </div>
      </div>
    );
  }

  const { share, document: doc } = data;

  return (
    <div className="min-h-screen bg-slate-50 flex flex-col justify-between">
      {/* Top Header */}
      <header className="bg-white border-b border-slate-200">
        <div className="max-w-4xl mx-auto px-4 h-16 flex items-center justify-between">
          <div className="flex items-center space-x-3">
            <div className="w-9 h-9 rounded-xl bg-indigo-600 flex items-center justify-center text-white">
              <ShieldCheck className="w-5 h-5" />
            </div>
            <div>
              <h1 className="text-sm font-bold text-slate-900">AI Document Locker</h1>
              <span className="text-[11px] text-slate-500">Secure Shared Document</span>
            </div>
          </div>
          <button
            onClick={onBackToApp}
            className="text-xs font-semibold text-indigo-600 hover:underline cursor-pointer"
          >
            Access My Locker &rarr;
          </button>
        </div>
      </header>

      {/* Main Body */}
      <main className="max-w-3xl mx-auto px-4 py-10 w-full">
        <div className="bg-white rounded-2xl border border-slate-200 shadow-xl overflow-hidden p-8 space-y-6">
          <div className="flex items-start justify-between">
            <div>
              <span className="text-xs font-bold px-2.5 py-0.5 rounded-full bg-indigo-100 text-indigo-700 uppercase tracking-wide">
                {doc.category}
              </span>
              <h2 className="text-2xl font-bold text-slate-900 mt-2">{doc.title}</h2>
              <p className="text-xs text-slate-400 font-mono mt-0.5">{doc.fileName}</p>
            </div>

            {share.permission === 'DOWNLOAD' ? (
              <button
                onClick={handleDownload}
                className="inline-flex items-center gap-2 px-4 py-2.5 text-xs font-semibold text-white bg-indigo-600 hover:bg-indigo-700 rounded-xl shadow-xs transition-colors cursor-pointer"
              >
                <Download className="w-4 h-4" />
                <span>Download File</span>
              </button>
            ) : (
              <div className="flex items-center gap-1.5 px-3 py-1.5 rounded-lg bg-slate-100 text-slate-600 text-xs font-semibold">
                <Lock className="w-3.5 h-3.5" />
                <span>View-Only Access</span>
              </div>
            )}
          </div>

          {/* AI Executive Summary */}
          {doc.summary && (
            <div className="p-5 rounded-2xl bg-amber-50/70 border border-amber-200/80">
              <div className="flex items-center gap-1.5 font-bold text-xs text-amber-950 mb-2">
                <Sparkles className="w-4 h-4 text-amber-600" />
                <span>AI Executive Summary</span>
              </div>
              <p className="text-xs text-slate-700 leading-relaxed">{doc.summary}</p>
            </div>
          )}

          {/* Document Content Preview */}
          {doc.extractedText && (
            <div>
              <h4 className="text-xs font-bold uppercase tracking-wider text-slate-500 mb-2">
                Document Preview
              </h4>
              <div className="p-4 bg-slate-900 text-slate-100 rounded-xl text-xs font-mono whitespace-pre-wrap max-h-72 overflow-y-auto leading-relaxed border border-slate-800">
                {doc.extractedText}
              </div>
            </div>
          )}

          {/* Security Notice Footer */}
          <div className="pt-4 border-t border-slate-100 flex items-center justify-between text-xs text-slate-400">
            <span className="flex items-center gap-1">
              <Calendar className="w-3.5 h-3.5" />
              Shared via secure encrypted token
            </span>
            <span>Protected by AI Document Locker</span>
          </div>
        </div>
      </main>

      <footer className="text-center py-6 text-xs text-slate-400 border-t border-slate-200">
        AI-Powered Digital Document Locker &copy; 2026
      </footer>
    </div>
  );
}
