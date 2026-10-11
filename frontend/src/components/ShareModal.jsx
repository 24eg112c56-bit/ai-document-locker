import { useState, useEffect } from 'react';
import { X, Share2, Copy, Check, Trash2, Clock, Shield, AlertCircle } from 'lucide-react';
import api from '../services/api';

export default function ShareModal({ doc, isOpen, onClose, onShareUpdated }) {
  const [shares, setShares] = useState([]);
  const [loading, setLoading] = useState(false);
  const [creating, setCreating] = useState(false);
  const [email, setEmail] = useState('');
  const [permission, setPermission] = useState('VIEW');
  const [expiresInDays, setExpiresInDays] = useState('7');
  const [copiedToken, setCopiedToken] = useState(null);
  const [error, setError] = useState(null);

  useEffect(() => {
    if (isOpen && doc) {
      loadShares();
    }
  }, [isOpen, doc]);

  const loadShares = async () => {
    setLoading(true);
    try {
      const res = await api.get(`/documents/${doc.id}/shares`);
      setShares(res.data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  if (!isOpen || !doc) return null;

  const handleCreateShare = async (e) => {
    e.preventDefault();
    setCreating(true);
    setError(null);

    try {
      const res = await api.post(`/documents/${doc.id}/share`, {
        sharedWithEmail: email || null,
        permission: permission,
        expiresInDays: expiresInDays ? parseInt(expiresInDays, 10) : null
      });

      setShares([res.data, ...shares]);
      setEmail('');
      if (onShareUpdated) onShareUpdated();
    } catch (err) {
      setError(err.response?.data?.message || 'Could not generate share link');
    } finally {
      setCreating(false);
    }
  };

  const handleRevoke = async (shareId) => {
    try {
      await api.delete(`/documents/shares/${shareId}`);
      setShares(shares.filter((s) => s.id !== shareId));
      if (onShareUpdated) onShareUpdated();
    } catch (err) {
      alert('Could not revoke link');
    }
  };

  const copyShareLink = (token) => {
    const fullUrl = `${window.location.origin}?share=${token}`;
    navigator.clipboard.writeText(fullUrl);
    setCopiedToken(token);
    setTimeout(() => setCopiedToken(null), 2500);
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/50 backdrop-blur-xs">
      <div className="bg-white rounded-2xl max-w-lg w-full border border-slate-200 shadow-xl overflow-hidden animate-in fade-in zoom-in-95 duration-150">
        {/* Header */}
        <div className="flex items-center justify-between px-6 py-4 border-b border-slate-100">
          <div className="flex items-center gap-2">
            <div className="p-2 rounded-lg bg-indigo-50 text-indigo-600">
              <Share2 className="w-5 h-5" />
            </div>
            <div>
              <h3 className="text-base font-bold text-slate-900">Share Securely</h3>
              <p className="text-xs text-slate-500 truncate max-w-xs">{doc.title}</p>
            </div>
          </div>
          <button
            onClick={onClose}
            className="text-slate-400 hover:text-slate-600 p-1.5 rounded-lg hover:bg-slate-100 transition-colors cursor-pointer"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Create Link Form */}
        <form onSubmit={handleCreateShare} className="p-6 border-b border-slate-100 space-y-4">
          {error && (
            <div className="p-3 bg-rose-50 border border-rose-200 rounded-xl flex items-center gap-2 text-xs text-rose-700">
              <AlertCircle className="w-4 h-4 shrink-0" />
              <span>{error}</span>
            </div>
          )}

          <div>
            <label className="block text-xs font-semibold text-slate-700 mb-1">
              Recipient Email (Optional)
            </label>
            <input
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              placeholder="e.g. mentor@review.com (leave empty for public link)"
              className="w-full px-3 py-2 text-xs border border-slate-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 outline-hidden transition-all"
            />
          </div>

          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="block text-xs font-semibold text-slate-700 mb-1">Permission</label>
              <select
                value={permission}
                onChange={(e) => setPermission(e.target.value)}
                className="w-full px-3 py-2 text-xs border border-slate-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 outline-hidden transition-all bg-white"
              >
                <option value="VIEW">👀 View Only</option>
                <option value="DOWNLOAD">📥 Allow Download</option>
              </select>
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-700 mb-1">Expiration</label>
              <select
                value={expiresInDays}
                onChange={(e) => setExpiresInDays(e.target.value)}
                className="w-full px-3 py-2 text-xs border border-slate-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 outline-hidden transition-all bg-white"
              >
                <option value="1">1 Day</option>
                <option value="7">7 Days</option>
                <option value="30">30 Days</option>
                <option value="0">Never Expires</option>
              </select>
            </div>
          </div>

          <button
            type="submit"
            disabled={creating}
            className="w-full py-2.5 text-xs font-semibold text-white bg-indigo-600 hover:bg-indigo-700 active:bg-indigo-800 disabled:opacity-50 rounded-lg shadow-xs shadow-indigo-200 transition-all cursor-pointer flex items-center justify-center gap-1.5"
          >
            <Shield className="w-4 h-4" />
            <span>{creating ? 'Generating Token...' : 'Create Secure Share Link'}</span>
          </button>
        </form>

        {/* Existing Shares List */}
        <div className="p-6 bg-slate-50/50">
          <h4 className="text-xs font-bold uppercase tracking-wider text-slate-500 mb-3">
            Active Share Links ({shares.length})
          </h4>

          {loading ? (
            <p className="text-xs text-slate-400">Loading links...</p>
          ) : shares.length === 0 ? (
            <p className="text-xs text-slate-400 italic">No share links active for this document.</p>
          ) : (
            <div className="space-y-2.5 max-h-48 overflow-y-auto">
              {shares.map((share) => (
                <div
                  key={share.id}
                  className="p-3 bg-white border border-slate-200 rounded-xl flex items-center justify-between shadow-2xs gap-3"
                >
                  <div className="min-w-0 flex-1">
                    <div className="flex items-center gap-2">
                      <span className="text-[10px] font-bold px-1.5 py-0.5 rounded bg-slate-100 text-slate-700">
                        {share.permission}
                      </span>
                      {share.sharedWithEmail && (
                        <span className="text-xs text-slate-700 truncate font-semibold">
                          {share.sharedWithEmail}
                        </span>
                      )}
                    </div>
                    <div className="flex items-center gap-1 text-[11px] text-slate-400 mt-1">
                      <Clock className="w-3 h-3" />
                      <span>{share.expiresAt ? `Expires ${new Date(share.expiresAt).toLocaleDateString()}` : 'Never expires'}</span>
                    </div>
                  </div>

                  <div className="flex items-center space-x-1 shrink-0">
                    <button
                      onClick={() => copyShareLink(share.shareToken)}
                      className="p-2 text-indigo-600 hover:bg-indigo-50 rounded-lg transition-colors cursor-pointer"
                      title="Copy Share Link"
                    >
                      {copiedToken === share.shareToken ? (
                        <Check className="w-4 h-4 text-emerald-600" />
                      ) : (
                        <Copy className="w-4 h-4" />
                      )}
                    </button>
                    <button
                      onClick={() => handleRevoke(share.id)}
                      className="p-2 text-slate-400 hover:text-rose-600 hover:bg-rose-50 rounded-lg transition-colors cursor-pointer"
                      title="Revoke Link"
                    >
                      <Trash2 className="w-4 h-4" />
                    </button>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
